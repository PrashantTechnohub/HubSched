package com.NakshatraTechnoHub.HubSched.Api;

import android.content.Context;

import com.NakshatraTechnoHub.HubSched.UtilHelper.LocalPreference;

public class Constant {

    public static final String BASE_URL = "http://192.168.0.183:5000/meet";
    public static final String LOGIN_URL = BASE_URL+"/login";
    public static final String MEET_ROOMS_URL = BASE_URL+"/rooms" ;
    public static final String EMP_LIST_URL = BASE_URL+"/employees";

    public static final String EMPLOYEE_UPDATE_URL = BASE_URL+"/update_employee";
    public static final String UPDATE_PROFILE_URL = BASE_URL+"/update_profile";
    public static final String EMP_PROFILE_URL = BASE_URL+"/profile";

    public static final String CREATE_ROOM_URL = BASE_URL+"/create_room" ;
    public static final String CREATE_EMP_URL = BASE_URL+"/create_employee" ;

    public static final String REMOVE_EMP_URL = BASE_URL+"/delete/employee/" ;
    public static final String REMOVE_ROOM_URL = BASE_URL+"/delete/room/" ;

    public static final String REMOVE_MEETING_URL = BASE_URL+"/create_room" ;
    public static final String EMP_STATUS_URL = BASE_URL+"/employee_status" ;
    public static final String MEETING_LIST_URL = BASE_URL+"/meeting_list";
    public static final String POST_CONTENT_VIEW_URL = BASE_URL+"/contentView";
    
    public static String withToken(String url, Context context){
        String token = LocalPreference.getToken(context);
        String str = url+"?token="+token;
        return str;
    }

}
