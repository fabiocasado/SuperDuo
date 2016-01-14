package barqsoft.footballscores;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    // Updated this data to 2015/2016 leagues. Future release should fetch each league id dynamically, at least once, since it won't change once its set
    public static final int SERIE_A = 401;
    public static final int PREMIER_LEGAUE = 398;
    public static final int CHAMPIONS_LEAGUE = 405;
    public static final int PRIMERA_DIVISION = 399;
    public static final int BUNDESLIGA = 403;
    public static String getLeague(int league_num)
    {
        switch (league_num)
        {
            case SERIE_A : return "Seria A";
            case PREMIER_LEGAUE : return "Premier League";
            case CHAMPIONS_LEAGUE : return "UEFA Champions League";
            case PRIMERA_DIVISION : return "Primera Division";
            case BUNDESLIGA : return "Bundesliga";
            default: return "Not known League Please report";
        }
    }
    public static String getMatchDay(int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE)
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }

    /**
     * In the future, each widget can have its own date assigned, but for now, we are going to use just one for all possible widgets
     * @return
     */
    public static long getWidgetDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE);
        return pref.getLong("WIDGET_DATE_PREF", new Date().getTime());
    }

    public static void setWidgetDate(Context context, long widgetDate) {
        SharedPreferences pref = context.getSharedPreferences("WIDGET_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("WIDGET_DATE_PREF", widgetDate);
        editor.apply();
    }

    public static CharSequence getTodayOrDateTimeFormat(Context context, long unixTime) {
        if (android.text.format.DateUtils.isToday(unixTime)) {
            return context.getString(R.string.today);
        } else {
            return DateFormat.getLongDateFormat(context).format(new Date(unixTime));
        }
    }
}
