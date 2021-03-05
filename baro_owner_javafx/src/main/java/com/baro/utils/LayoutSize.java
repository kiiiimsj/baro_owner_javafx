package com.baro.utils;

public class LayoutSize {
    /**
     * Main Attribute Size
     */
    public static int TOP_BAR_HEIGHT = 80;

    public static int LOGIN_PAGE_WIDTH = 500;
    public static int LOGIN_PAGE_HEIGHT = 580;

    public static int MAIN_PAGE_WIDTH = 1000;
    public static int MAIN_PAGE_HEIGHT = 800;

    public static int MAIN_TAB_PANE_WIDTH = 120;
    public static int MAIN_TAB_PANE_HEIGHT = 120;

    public static int INSIDE_PANE_WIDTH = MAIN_PAGE_WIDTH - MAIN_TAB_PANE_WIDTH;
    public static int INSIDE_PANE_HEIGHT = MAIN_PAGE_HEIGHT - TOP_BAR_HEIGHT;

    /**
     * Order List Attribute Size
     **/
    public static int ORDER_LIST_WIDTH = 250;
    public static int ORDER_LIST_HEIGHT = 630;

    public static int ORDER_LIST_ORDER_CELL_HEIGHT = 95;

    public static int ORDER_CELL_ORDER_TIME_WIDTH = 70;

    public static int ORDER_LIST_TOP_AREA_HEIGHT = 80;
    public static int ORDER_LIST_BOTTOM_AREA_HEIGHT = 120;
    //460 + padding L 10 R 10
    public static int ORDER_LIST_TOP_AREA_HBOX_WIDTH = 480;

    public static int ORDER_DETAIL_CANCEL_BUTTON_WIDTH = INSIDE_PANE_WIDTH - ORDER_LIST_WIDTH - ORDER_LIST_TOP_AREA_HBOX_WIDTH;

    public static int ORDER_DETAIL_WIDTH = INSIDE_PANE_WIDTH - ORDER_LIST_WIDTH;
    public static int ORDER_DETAIL_HEIGHT = INSIDE_PANE_HEIGHT;

    public static int ORDER_MENUS_WIDTH = ORDER_DETAIL_WIDTH / 2;
    public static int ORDER_MENUS_HEIGHT = ORDER_DETAIL_HEIGHT;

    public static int ORDER_INFO_WIDTH = ORDER_DETAIL_WIDTH / 2;
    public static int ORDER_REQUEST_WIDTH = ORDER_DETAIL_WIDTH / 2;



    public static class DialogWidthHeight {
        public static int DISCOUNT_RATE_PAGE_WIDTH = 300;
        public static int DISCOUNT_RATE_PAGE_HEIGHT = 200;
    }
}
