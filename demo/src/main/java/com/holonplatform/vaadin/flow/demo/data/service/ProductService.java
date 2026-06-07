package com.holonplatform.vaadin.flow.demo.data.service;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.beans.BeanDatastoreHelper;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Service layer for {@link Product} CRUD operations.
 *
 * <p>Delegates all persistence to {@link BeanDatastoreHelper}, which wraps the Holon
 * {@link com.holonplatform.core.datastore.beans.BeanDatastore} — no manual
 * {@code PropertyBox} conversions. The underlying JPA Datastore is
 * auto-configured via {@code holon-datastore-jpa-spring-boot}.
 */
@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    // ── Holon property definitions (used for raw query filters / sorts) ───────
    private static final DataTarget<?>         TARGET        = DataTarget.named("product");
    private static final StringProperty        NAME_PROP     = StringProperty.create("name");
    private static final StringProperty        CATEGORY_PROP = StringProperty.create("category");
    private static final PathProperty<Long>    ID_PROP       = PathProperty.create("id", Long.class);
    private static final PathProperty<Boolean> ACTIVE_PROP   = PathProperty.create("active", Boolean.class);

    // ── CRUD delegate ─────────────────────────────────────────────────────────
    private final BeanDatastoreHelper<Product> helper;

    public ProductService(Datastore datastore) {
        this.helper = BeanDatastoreHelper.of(datastore, Product.class);
    }

    // ── Read operations ───────────────────────────────────────────────────────

    public Optional<Product> findById(Long id) {
        return helper.findOne(ID_PROP.eq(id));
    }

    public List<Product> findAll() {
        return helper.getDatastore()
                .query(TARGET)
                .sort(NAME_PROP.asc())
                .stream(BeanProjection.of(Product.class))
                .toList();
    }

    public List<Product> findActive() {
        try (Stream<Product> s = helper.findAll(ACTIVE_PROP.eq(true))) {
            return s.toList();
        }
    }

    public List<Product> search(String text) {
        if (text == null || text.isBlank()) return findAll();
        try (Stream<Product> s = helper.findAll(nameOrCategoryFilter(text))) {
            return s.toList();
        }
    }

    /**
     * Paginated stream used by the Listing bundle fetch callback.
     *
     * @param offset first row index (0-based)
     * @param limit  max number of rows
     * @param text   optional search filter (name or category)
     */
    public Stream<Product> fetch(int offset, int limit, String text) {
        var q = helper.getDatastore()
                .query(TARGET)
                .restrict(limit, offset)
                .sort(NAME_PROP.asc());
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        return q.stream(BeanProjection.of(Product.class));
    }

    public long count(String text) {
        var q = helper.getDatastore().query(TARGET);
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        return q.count();
    }

    /**
     * Paginated stream filtered by both a text search and an arbitrary {@link QueryFilter}
     * (e.g. the one produced by {@link com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel}).
     * Both filters are AND-combined when both are non-null/non-blank.
     * Applies the given {@link QuerySort} if non-null, otherwise falls back to name ascending.
     */
    public Stream<Product> fetch(int offset, int limit, String text, QueryFilter filter, QuerySort sort) {
        var q = helper.getDatastore()
                .query(TARGET)
                .restrict(limit, offset);
        if (sort != null) {
            q = q.sort(sort);
        } else {
            q = q.sort(NAME_PROP.asc());
        }
        if (text != null && !text.isBlank()) {
            q = q.filter(nameOrCategoryFilter(text));
        }
        if (filter != null) {
            q = q.filter(filter);
        }
        return q.stream(BeanProjection.of(Product.class));
    }

    // ── Write operations ──────────────────────────────────────────────────────

    @Transactional
    public Product save(Product product) {
        log.info("Saving product: {}", product.getName());
        return helper.save(product).getResult().orElse(product);
    }

    @Transactional
    public void delete(Product product) {
        log.info("Deleting product id={}", product.getId());
        helper.delete(product);
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting product id={}", id);
        findById(id).ifPresent(helper::delete);
    }

    // ── Seed data ────────────────────────────────────────────────────────────

    /**
     * Inserts sample data when the database is empty.
     * Called from {@link com.holonplatform.vaadin.flow.demo.data.DemoDataInitializer}.
     */
    @Transactional
    public void seedIfEmpty() {
        if (helper.getDatastore().query(TARGET).count() > 0) return;

        String[] categories = {"Electronics", "Furniture", "Audio", "Lighting", "Accessories",
                "Storage", "Networking", "Software", "Peripherals", "Office Supplies"};
        String[][] prefixes = {
            {"Wireless", "Bluetooth", "USB-C", "4K", "Pro", "Mini", "Ultra", "Smart", "Portable", "Compact"},
            {"Ergonomic", "Standing", "Adjustable", "Modular", "Foldable", "Premium", "Executive", "Industrial", "Wooden", "Metal"},
            {"Noise-Cancelling", "Studio", "Surround", "Bass", "Hi-Fi", "Wireless", "Gaming", "DJ", "Podcast", "Mono"},
            {"LED", "RGB", "Solar", "Motion", "Dimmable", "Touch", "Smart", "Ambient", "Task", "Clip-On"},
            {"Multi-Port", "Cable", "Desk", "Travel", "Eco", "Universal", "Quick", "Heavy-Duty", "Slim", "Anti-Slip"},
            {"SSD", "NAS", "Cloud", "RAID", "Encrypted", "Portable", "High-Speed", "Archive", "Backup", "Flash"},
            {"Mesh", "5G", "WiFi-6", "Gigabit", "Fiber", "PoE", "Dual-Band", "Enterprise", "VPN", "Outdoor"},
            {"Antivirus", "Productivity", "Creative", "Database", "Analytics", "CRM", "ERP", "CAD", "IDE", "Backup"},
            {"Mechanical", "Optical", "Touch", "Pen", "Trackball", "Ergonomic", "Split", "Macro", "Silent", "TKL"},
            {"Recycled", "Premium", "Bulk", "Custom", "Branded", "Standard", "Archive", "Lined", "Grid", "Sticky"}
        };
        String[][] nouns = {
            {"Mouse", "Keyboard", "Monitor", "Webcam", "Hub", "Charger", "Speaker", "Tablet", "Dock", "Adapter"},
            {"Chair", "Desk", "Shelf", "Cabinet", "Drawer", "Podium", "Table", "Bench", "Rack", "Divider"},
            {"Headset", "Earbuds", "Microphone", "Amplifier", "Mixer", "DAC", "Soundbar", "Subwoofer", "Interface", "Receiver"},
            {"Lamp", "Strip", "Bulb", "Panel", "Fixture", "Pendant", "Chandelier", "Sconce", "Floodlight", "Spotlight"},
            {"Mat", "Organizer", "Mount", "Stand", "Holder", "Clip", "Tray", "Hook", "Pad", "Cover"},
            {"Drive", "Enclosure", "Card", "Stick", "Bay", "Array", "Dock", "Reader", "Caddy", "Module"},
            {"Router", "Switch", "Access Point", "Modem", "Repeater", "Bridge", "Firewall", "Controller", "Antenna", "Cable"},
            {"Suite", "License", "Plugin", "Module", "Toolkit", "Platform", "Service", "Engine", "Framework", "Agent"},
            {"Keyboard", "Mouse", "Tablet", "Stylus", "Controller", "Joystick", "Wheel", "Pad", "Scanner", "Printer"},
            {"Paper", "Notebook", "Binder", "Folder", "Envelope", "Label", "Tape", "Pen", "Marker", "Stapler"}
        };

        var rng = new java.util.Random(42);
        List<Product> batch = new java.util.ArrayList<>(500);
        int total = 10_000;

        for (int i = 0; i < total; i++) {
            int catIdx = i % categories.length;
            String prefix = prefixes[catIdx][rng.nextInt(prefixes[catIdx].length)];
            String noun = nouns[catIdx][rng.nextInt(nouns[catIdx].length)];
            String name = prefix + " " + noun + " " + (i + 1);
            BigDecimal price = BigDecimal.valueOf(rng.nextInt(99900) + 100, 2); // $1.00 – $999.99
            boolean active = rng.nextInt(10) > 1; // 90% active
            Product p = new Product(name, categories[catIdx], price);
            p.setActive(active);
            batch.add(p);

            if (batch.size() == 500) {
                helper.bulkInsert(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            helper.bulkInsert(batch);
        }
        log.info("Seeded {} demo products", total);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static QueryFilter nameOrCategoryFilter(String text) {
        return NAME_PROP.containsIgnoreCase(text)
                .or(CATEGORY_PROP.containsIgnoreCase(text));
    }

    /**
     * Column-aware paginated stream for {@link com.holonplatform.vaadin.flow.components.ListingBundleBuilder.ColumnAwareFilteredFetchCallback}.
     *
     * <p>The {@code columns} list carries the visible column property names configured
     * via {@code .columns("name", "price", ...)} in the listing builder. Use it to
     * skip fetching heavy fields (e.g. binary blobs, large text) that are not displayed.
     * When the list is <strong>empty</strong> (columns not explicitly configured) all
     * fields are fetched — safe fallback.</p>
     *
     * <p>For simple entities like {@link Product} whose fields are all lightweight,
     * the canonical approach is to check the list and fall back to a full fetch when
     * it is empty. If you want true SQL projection, build a {@code PropertySet} from
     * the column names and use a {@code PropertyBox} stream instead.</p>
     *
     * @param offset  first row index (0-based)
     * @param limit   max rows
     * @param text    optional name/category text filter
     * @param filter  optional structured filter from DynamicFilterPanel (may be null)
     * @param sort    optional sort from grid column headers (null → name asc)
     * @param columns visible column names; empty means "all columns"
     */
    public Stream<Product> fetch(int offset, int limit, String text,
                                 QueryFilter filter, QuerySort sort, List<String> columns) {
        // columns is empty when .columns(…) was not called — fetch everything
        // For a real projection use-case you would build a PropertySet from the
        // column names here and use a PropertyBox stream instead of BeanProjection.
        QueryFilter textFilter = null;
        if (text != null && !text.isBlank()) {
            textFilter = nameOrCategoryFilter(text);
        }

        if (filter != null && textFilter != null) {
            filter = filter.and(textFilter);
        } 

        return helper.findSlice(offset, limit, filter, sort,columns);
    }
}
