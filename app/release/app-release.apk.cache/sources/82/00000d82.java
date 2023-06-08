package com.NakshatraTechnoHub.HubSched.Api;

import android.content.Context;
import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;

/* loaded from: classes.dex */
public class Constant {
    public static final String BASE_URL = "http://192.168.0.120:5000/meet";
    public static final String CREATE_EMP_URL = "http://192.168.0.120:5000/meet/create_employee";
    public static final String CREATE_MEETING_URL = "http://192.168.0.120:5000/meet/schedule_meet";
    public static final String CREATE_ROOM_URL = "http://192.168.0.120:5000/meet/create_room";
    public static final String EMPLOYEE_LIST_FOR_MEET_URL = "http://192.168.0.120:5000/meet/employee_list_for_meet";
    public static final String EMPLOYEE_UPDATE_URL = "http://192.168.0.120:5000/meet/update_employee";
    public static final String EMP_LIST_URL = "http://192.168.0.120:5000/meet/employees";
    public static final String EMP_PROFILE_URL = "http://192.168.0.120:5000/meet/profile";
    public static final String EMP_STATUS_URL = "http://192.168.0.120:5000/meet/employee_status";
    public static final String LOGIN_URL = "http://192.168.0.120:5000/meet/login";
    public static final String MEETING_LIST_URL = "http://192.168.0.120:5000/meet/meeting_list";
    public static final String MEETS_FOR_DATE_URL = "http://192.168.0.120:5000/meet/meets_for_date";
    public static final String MEET_ROOMS_URL = "http://192.168.0.120:5000/meet/rooms";
    public static final String POST_CONTENT_VIEW_URL = "http://192.168.0.120:5000/meet/contentView";
    public static final String REMOVE_EMP_URL = "http://192.168.0.120:5000/meet/delete/employee/";
    public static final String REMOVE_MEETING_URL = "http://192.168.0.120:5000/meet/create_room";
    public static final String REMOVE_ROOM_URL = "http://192.168.0.120:5000/meet/delete/room/";
    public static final String UPDATE_PROFILE_URL = "http://192.168.0.120:5000/meet/update_profile";

    public static String withToken(String str, Context context) {
        return str + "?token=" + LocalPreference.getToken(context);
    }
}