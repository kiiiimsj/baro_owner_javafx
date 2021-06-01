package com.baro.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class LayoutSize {
    /**
     * Window Attribute Size
     */
    public static Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    public static double CENTER_X
            = bounds.getMinX() + (bounds.getWidth() - LayoutSize.MAIN_PAGE_WIDTH)
            * (1.0f / 3);
    public static double CENTER_Y
            = bounds.getMinY() + (bounds.getHeight() - LayoutSize.MAIN_PAGE_HEIGHT) * (1.0f / 4);
    public static double CENTER_IN_PARENT_Y = bounds.getMinY() + (bounds.getHeight() - LayoutSize.MAIN_PAGE_HEIGHT) * (1.0f / 2.2);
    public static double CENTER_IN_PARENT_X = bounds.getMinX() + (bounds.getWidth() - LayoutSize.MAIN_PAGE_WIDTH) * (1.0f / 2.1);
    /**
     * Dialog Attribute Size
     */
    public static int DIALOG_TOP_BAR_HEIGHT = 50;
    public static int DIALOG_WIDTH = 300;
    public static int DIALOG_HEIGHT = 250;
    public static int DIALOG_DISCOUNT_WIDTH = 300;
    public static int DIALOG_DISCOUNT_HEIGHT = 280;
    /**
     * Main Attribute Size
     */
    public static int TOP_BAR_HEIGHT = 70; //80

    public static int LOGIN_PAGE_WIDTH = 400; //500
    public static int LOGIN_PAGE_HEIGHT = 580;

    public static int MAIN_PAGE_WIDTH = 800; //1000
    public static int MAIN_PAGE_HEIGHT = 730; //800

    public static int MAIN_TAB_PANE_WIDTH = 100;
    public static int MAIN_TAB_PANE_HEIGHT = 110;

    public static int INSIDE_PANE_WIDTH = MAIN_PAGE_WIDTH - MAIN_TAB_PANE_WIDTH;
    //padding 20
    public static int INSIDE_PANE_HEIGHT = MAIN_PAGE_HEIGHT - TOP_BAR_HEIGHT;

    /**
     * Order List Attribute Size
     **/
    public static int ORDER_LIST_WIDTH = 180;
    public static int ORDER_LIST_HEIGHT = 610;

    public static int ORDER_LIST_ORDER_CELL_HEIGHT = 30;

    public static int ORDER_LIST_TOP_AREA_HEIGHT = 80;
    public static int ORDER_LIST_BOTTOM_AREA_HEIGHT = 120;
    //460 + padding L 10 R 10
    public static int ORDER_LIST_TOP_AREA_HBOX_WIDTH = 480;

    public static int ORDER_DETAIL_CANCEL_BUTTON_WIDTH = INSIDE_PANE_WIDTH - ORDER_LIST_WIDTH - ORDER_LIST_TOP_AREA_HBOX_WIDTH;

    public static int ORDER_DETAIL_WIDTH = INSIDE_PANE_WIDTH - ORDER_LIST_WIDTH - 15; //padding 15
    public static int ORDER_DETAIL_HEIGHT = INSIDE_PANE_HEIGHT;

    public static int ORDER_MENUS_WIDTH = (ORDER_DETAIL_WIDTH / 2) - 5;
    public static int BOTTOM_BUTTON_WIDTH = ORDER_DETAIL_WIDTH;
    /**
     * Order History Attribute Size
     **/
    public static int ORDER_HISTORY_TOP_AREA_WIDTH = 180;
    /**
     * Statistics Attribute Size
     **/
    public static int STATISTICS_TAB_BUTTON_WIDTH = 180;
    public static int STATISTICS_DAILY_SELL_HEIGHT = 300;
    /**
     * Setting Attribute Size
     **/
    public static int PRINT_LABEL_SPACING = 5;
    public static int PRINT_LABEL_COUNT = 5;
    public static int PRINT_LABEL_WIDTH = (INSIDE_PANE_WIDTH / PRINT_LABEL_COUNT) - (PRINT_LABEL_SPACING * PRINT_LABEL_COUNT);
    public static int PRINT_LABEL_HEIGHT = 80;
    public static int COMBO_BOX_WIDTH = 400;
}
