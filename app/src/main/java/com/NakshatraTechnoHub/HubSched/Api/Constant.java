package com.NakshatraTechnoHub.HubSched.Api;

import android.content.Context;

import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;

public class Constant {

    //public static final String BASE_URL = "http://192.168.0.120:5000/meet"; //Jab Sir nahi rhange

    public static final String domain = "http://192.168.0.242:5000";
    //public static final String domain = "https://3423-182-48-252-114.ngrok-free.app/";
    public static final String BASE_URL = domain + "/meet";
    public static final String CHAT_URL = BASE_URL + "/send_message";
    public static final String GET_CHAT_URL = BASE_URL + "/get_chats/";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String MEET_ROOMS_URL = BASE_URL + "/rooms";

    public static final String LOGOUT_URL = BASE_URL + "/logout";

    public static final String LOGOUT_PANTRY_URL = BASE_URL + "/pantry_logout";
    public static final String MEET_REQUEST_URL = BASE_URL + "/meet_request";
    public static final String DELETE_MEETING_URL = BASE_URL + "/deleteMeeting";
    public static final String SUPPORT_URL = BASE_URL + "/support";
    public static final String SUPPORT_LIST_URL = BASE_URL + "/support_list";
    public static final String SUPPORT_STATUS_URL = BASE_URL + "/support_status";
    public static final String EMP_LIST_URL = BASE_URL + "/employees";
    public static final String UPDATE_PROFILE_URL = BASE_URL + "/update_profile";

    public static final String UPDATE_EMP_BY_ADMIN_URL = BASE_URL + "/update_employee";
    public static final String EMP_PROFILE_URL = BASE_URL + "/profile";
    public static final String CREATE_ROOM_URL = BASE_URL + "/create_room";
    public static final String CREATE_EMP_URL = BASE_URL + "/create_employee";
    public static final String CREATE_MEETING_URL = BASE_URL + "/schedule_meet";
    public static final String MEETS_FOR_DATE_URL = BASE_URL + "/meets_for_date";
    public static final String EMPLOYEE_LIST_FOR_MEET_URL = BASE_URL + "/employee_list_for_meet";
    public static final String REMOVE_EMP_URL = BASE_URL + "/delete/employee/";
    public static final String REMOVE_ROOM_URL = BASE_URL + "/delete/room/";
    public static final String EMP_STATUS_URL = BASE_URL + "/employee_status";
    public static final String MEETING_LIST_URL = BASE_URL + "/meeting_list";
    public static final String GENERATE_QR_CODE_URL = BASE_URL + "/generate_scancode";
    public static final String ATTENDENCE_TEST_URL = BASE_URL + "/attendence_test";

    public static final String PANTRY_RESPOND_REQUEST_URL = BASE_URL + "/respond_pantry_request";
    public static final String PANTRY_REQUEST_URL = BASE_URL + "/request_pantry";
    public static final String TRIGGER_PANTRY = BASE_URL + "/trigger_pantry";
    public static final String GET_TRIGGER_PANTRY = BASE_URL + "/get_pantry_orders";
    public static final String BANNER_LIST = BASE_URL + "/bannerList";

    public static String withToken(String url, Context context) {
        String token = LocalPreference.getToken(context);
        String str = url + "?token=" + token;
        return str;
    }

}
