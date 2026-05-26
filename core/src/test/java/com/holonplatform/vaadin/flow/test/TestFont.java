package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestFont {

    @Test
    void testSizeClassNames() {
        assertEquals("font-size-xxsmall", Font.Size.XXSMALL.getClassName());
        assertEquals("font-size-small", Font.Size.SMALL.getClassName());
        assertEquals("font-size-medium", Font.Size.MEDIUM.getClassName());
        assertEquals("font-size-large", Font.Size.LARGE.getClassName());
        assertEquals("font-size-xxxlarge", Font.Size.XXXLARGE.getClassName());
    }

    @Test
    void testAllSizesHaveClassNames() {
        for (Font.Size size : Font.Size.values()) {
            assertNotNull(size.getClassName());
            assertTrue(size.getClassName().startsWith("font-size-"));
        }
    }

    @Test
    void testWeightClassNames() {
        assertEquals("font-weight-thin", Font.Weight.THIN.getClassName());
        assertEquals("font-weight-normal", Font.Weight.NORMAL.getClassName());
        assertEquals("font-weight-bold", Font.Weight.BOLD.getClassName());
        assertEquals("font-weight-black", Font.Weight.BLACK.getClassName());
    }

    @Test
    void testAllWeightsHaveClassNames() {
        for (Font.Weight weight : Font.Weight.values()) {
            assertNotNull(weight.getClassName());
            assertTrue(weight.getClassName().startsWith("font-weight-"));
        }
    }

    @Test
    void testLineHeightClassNames() {
        assertEquals("line-height-none", Font.LineHeight.NONE.getClassName());
        assertEquals("line-height-small", Font.LineHeight.SMALL.getClassName());
        assertEquals("line-height-medium", Font.LineHeight.MEDIUM.getClassName());
    }

    @Test
    void testAllLineHeightsHaveClassNames() {
        for (Font.LineHeight lh : Font.LineHeight.values()) {
            assertNotNull(lh.getClassName());
            assertTrue(lh.getClassName().startsWith("line-height-"));
        }
    }
}
