package com.holonplatform.vaadin.flow;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;

public interface HasOptionsBar {

    MenuItem createSrtBy(MenuBar menuBar);

    MenuItem importEntity(MenuBar menuBar);

    MenuItem exportEntity(MenuBar menuBar);

    MenuItem refreshList(MenuBar menuBar);

    MenuItem advancedSearch(MenuBar menuBar);


}
