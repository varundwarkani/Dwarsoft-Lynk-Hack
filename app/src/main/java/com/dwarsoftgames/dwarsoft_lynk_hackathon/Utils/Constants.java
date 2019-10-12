package com.dwarsoftgames.dwarsoft_lynk_hackathon.Utils;

public class Constants {

    public static final String SHAREDPREF = "SharedPreference";

    private static final String ENDPOINT = "http://172.18.13.187:3004/api/";

    public static final String AUTH = ENDPOINT + "volunteerauth";

    public static final String AUTH_DATA = ENDPOINT + "volunteerinsert";

    public static final String STATES = ENDPOINT + "getstates";

    public static final String CITY = ENDPOINT + "getcities";

    public static final String AREA = ENDPOINT + "getareas";

    public static final String GET_GROUP_DETAILS = ENDPOINT + "getgroupdetails";

    public static final String CREATE_GROUP = ENDPOINT + "creategroup";

    public static final String GET_HELP_TYPES = ENDPOINT + "gethelpertypes";

    public static final String SHARE_RESOURCES = ENDPOINT + "helpsomeone";

    public static final String GET_VICTIM_DETAILS = ENDPOINT + "getvictimdetails";

    public static final String GET_VOLUNTEER_DETAILS = ENDPOINT + "getvolunteerdetails";

    public static final String REQUEST_HELP = ENDPOINT + "posthelpvictim";
}