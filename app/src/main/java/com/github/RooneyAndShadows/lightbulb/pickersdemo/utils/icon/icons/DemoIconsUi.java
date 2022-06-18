package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons;

import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.IDemoIcon;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome;


import java.util.HashMap;
import java.util.Map;

public enum DemoIconsUi implements IDemoIcon {

    /**
     * MENU
     **/
    ICON_MENU_SETTINGS(1, FontAwesome.Icon.faw_cog),
    ICON_MENU_SUBSCRIPTION(2, FontAwesome.Icon.faw_award),
    ICON_MENU_NOTIFICATIONS(3, FontAwesome.Icon.faw_bell1),
    ICON_MENU_HOME(4, FontAwesome.Icon.faw_home),
    ICON_MENU_BUDGET(5, FontAwesome.Icon.faw_funnel_dollar),
    ICON_MENU_CATEGORY(6, FontAwesome.Icon.faw_shapes),
    ICON_MENU_TRANSACTIONS(7, FontAwesome.Icon.faw_money_check_alt),
    ICON_MENU_TRANSACTION_TEMPLATES(8, FontAwesome.Icon.faw_file_invoice),
    ICON_MENU_TRANSACTION_EXECUTED(9, FontAwesome.Icon.faw_clipboard_check),
    ICON_MENU_TRANSACTION_UPCOMING(10, FontAwesome.Icon.faw_hourglass_start),
    ICON_MENU_TRANSACTION_LATE(11, FontAwesome.Icon.faw_history),
    ICON_MENU_RECURRING_RULE(12, FontAwesome.Icon.faw_sync),
    ICON_MENU_SAVINGS(13, FontAwesome.Icon.faw_piggy_bank),
    ICON_MENU_CURRENCY_WALLET(14, FontAwesome.Icon.faw_wallet),
    ICON_MENU_LOGOUT(15, FontAwesome.Icon.faw_sign_out_alt),
    /**
     * SYSTEM
     **/
    ICON_SYSTEM_BACK(101, FontAwesome.Icon.faw_arrow_left),
    ICON_SYSTEM_MENU(102, FontAwesome.Icon.faw_bars),
    ICON_SYSTEM_ADD(103, FontAwesome.Icon.faw_plus),
    ICON_SYSTEM_EDIT(104, FontAwesome.Icon.faw_pencil_alt),
    ICON_SYSTEM_INFO(105, FontAwesome.Icon.faw_info),
    ICON_SYSTEM_EDIT_ALT(106, FontAwesome.Icon.faw_edit),
    ICON_SYSTEM_SAVE(107, FontAwesome.Icon.faw_check),
    ICON_SYSTEM_CONFIRM(108, FontAwesome.Icon.faw_check),
    ICON_SYSTEM_ACTIVATE(109, FontAwesome.Icon.faw_check),
    ICON_SYSTEM_DEACTIVATE(110, FontAwesome.Icon.faw_times),
    ICON_SYSTEM_SAVING_OPERATION(111, FontAwesome.Icon.faw_piggy_bank),
    ICON_SYSTEM_CREATE_FROM_TEMPLATE(112, FontAwesome.Icon.faw_file_invoice),
    ICON_SYSTEM_PREVIEW(113, FontAwesome.Icon.faw_eye1),
    ICON_SYSTEM_SETTINGS(114, FontAwesome.Icon.faw_cog),
    ICON_SYSTEM_MORE(115, FontAwesome.Icon.faw_ellipsis_v),
    ICON_SYSTEM_SYNC(116, FontAwesome.Icon.faw_sync),
    ICON_SYSTEM_COPY(117, FontAwesome.Icon.faw_copy),
    ICON_SYSTEM_FILTER(118, FontAwesome.Icon.faw_filter),
    ICON_SYSTEM_DELETE(119, FontAwesome.Icon.faw_trash),
    ICON_SYSTEM_DISABLED(120, FontAwesome.Icon.faw_eye_slash),
    ICON_SYSTEM_UNDO(121, FontAwesome.Icon.faw_undo_alt),
    ICON_SYSTEM_SELECT_ALL(122, FontAwesome.Icon.faw_clipboard_check),
    ICON_SYSTEM_FORWARD(123, FontAwesome.Icon.faw_chevron_right),
    ICON_SYSTEM_LINK(124, FontAwesome.Icon.faw_link),
    ICON_SYSTEM_SELECTED(125, FontAwesome.Icon.faw_check),
    ICON_SYSTEM_THEME(126, FontAwesome.Icon.faw_palette),
    ICON_SYSTEM_LOCALE(127, FontAwesome.Icon.faw_language),
    ICON_SYSTEM_LABEL(128, FontAwesome.Icon.faw_tags),
    /**
     * NOTIFICATIONS
     */
    ICON_NOTIFICATION_UPCOMING(201, FontAwesome.Icon.faw_hourglass_start),
    ICON_NOTIFICATION_LATE(202, FontAwesome.Icon.faw_history),
    ICON_NOTIFICATION_BUDGET_OVERSPENT(203, FontAwesome.Icon.faw_percentage),
    ICON_NOTIFICATION_USER_INACTIVE(204, FontAwesome.Icon.faw_ghost),
    /**
     * INDICATORS
     **/
    ICON_INDICATOR_INCOME(301, FontAwesome.Icon.faw_angle_double_up),
    ICON_INDICATOR_LABEL(302, FontAwesome.Icon.faw_tags),
    ICON_INDICATOR_EXPENSE(303, FontAwesome.Icon.faw_angle_double_down),
    ICON_INDICATOR_EXECUTED(304, FontAwesome.Icon.faw_clipboard_check),
    ICON_INDICATOR_UPCOMING(305, FontAwesome.Icon.faw_hourglass_start),
    ICON_INDICATOR_LATE(306, FontAwesome.Icon.faw_history),
    ICON_INDICATOR_SUCCESS(307, FontAwesome.Icon.faw_check),
    ICON_INDICATOR_RECURRING_TRANSACTION(308, FontAwesome.Icon.faw_sync),
    ICON_INDICATOR_SAVING_TRANSACTION(309, FontAwesome.Icon.faw_piggy_bank),
    ICON_INDICATOR_HAS_MORE_ITEMS_INFINITE(310, FontAwesome.Icon.faw_infinity),
    ICON_INDICATOR_HAS_MORE_ITEMS_DETERMINED(311, FontAwesome.Icon.faw_ellipsis_h),
    ICON_INDICATOR_SAVING_OPERATION_DEPOSIT(312, FontAwesome.Icon.faw_angle_double_up),
    ICON_INDICATOR_SAVING_OPERATION_WITHDRAW(313, FontAwesome.Icon.faw_angle_double_down),
    /**
     * OTHER
     **/
    ICON_USER(401, FontAwesome.Icon.faw_user_alt),
    ICON_PASSWORD(402, FontAwesome.Icon.faw_lock),
    ICON_KEY(403, FontAwesome.Icon.faw_key),
    ICON_EMAIL(404, FontAwesome.Icon.faw_at),
    ICON_TRANSACTION(405, FontAwesome.Icon.faw_money_check_alt),
    ICON_BALANCE(406, FontAwesome.Icon.faw_balance_scale),
    ICON_ACTIVITY(407, FontAwesome.Icon.faw_clipboard_list),
    ICON_BREAKDOWN(408, FontAwesome.Icon.faw_percentage),
    ICON_FREQUENCY_TYPE(409, FontAwesome.Icon.faw_hourglass_end),
    ICON_CALENDAR(410, FontAwesome.Icon.faw_calendar_alt),
    ICON_CLOCK(411, FontAwesome.Icon.faw_clock),
    ICON_REPEAT(412, FontAwesome.Icon.faw_sync),
    ICON_PICKER_ABSTRACT_TYPE(413, FontAwesome.Icon.faw_clipboard_list),
    ICON_PICKER_LABEL(414, FontAwesome.Icon.faw_tags),
    ICON_PICKER_LOCALE(415, FontAwesome.Icon.faw_globe),
    ICON_BAR_CHART(416, FontAwesome.Icon.faw_chart_bar),
    ICON_CATEGORY(417, FontAwesome.Icon.faw_shapes);

    private final Integer value;
    private final IIcon icon;
    private static final Map<Integer, DemoIconsUi> mapValues = new HashMap<>();

    DemoIconsUi(Integer value, IIcon icon) {
        this.value = value;
        this.icon = icon;
    }

    static {
        for (DemoIconsUi icon : DemoIconsUi.values()) {
            mapValues.put(icon.value, icon);
        }
    }

    public static DemoIconsUi valueOf(Integer icon) {
        return (DemoIconsUi) mapValues.get(icon);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public IIcon getIcon() {
        return icon;
    }
}