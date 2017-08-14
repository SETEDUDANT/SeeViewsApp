package com.seeviews.utils;

import com.seeviews.R;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public class RatingUtils {

    public static int getRatingColor(float rating){
//        switch (rating){
//            case 1:return R.color.rating1;
//            case 2:return R.color.rating2;
//            case 3:return R.color.rating3;
//            case 4:return R.color.rating4;
//            case 5:return R.color.rating5;
//            case 6:return R.color.rating6;
//            case 7:return R.color.rating7;
//            case 8:return R.color.rating8;
//            case 9:return R.color.rating9;
//            case 10:return R.color.rating10;
//            default:return R.color.rating10;
//        }
        if (rating < 5)
            return R.color.ratingBad;
        else if (rating < 7)
            return R.color.ratingNeutral;
        else
            return R.color.ratingGood;
    }
}
