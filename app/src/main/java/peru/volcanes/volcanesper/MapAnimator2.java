package peru.volcanes.volcanesper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapAnimator2 {

    private static MapAnimator2 mapAnimator2;

    private Polyline backgroundPolyline;

    private Polyline foregroundPolyline;

    private PolylineOptions optionsForeground;

    private AnimatorSet firstRunAnimSet;

    private AnimatorSet secondLoopRunAnimSet;

    static final int GREY = Color.parseColor("#FFA7A6A6");


    private MapAnimator2(){

    }

    public static MapAnimator2 getInstance(){
        if(mapAnimator2 == null) mapAnimator2 = new MapAnimator2();
        return mapAnimator2;
    }




    public void animateRoute2(GoogleMap googleMap, List<LatLng> bangaloreRoute2) {
        if (firstRunAnimSet == null){
            firstRunAnimSet = new AnimatorSet();
        } else {
            firstRunAnimSet.removeAllListeners();
            firstRunAnimSet.end();
            firstRunAnimSet.cancel();

            firstRunAnimSet = new AnimatorSet();
        }
        if (secondLoopRunAnimSet == null) {
            secondLoopRunAnimSet = new AnimatorSet();
        } else {
            secondLoopRunAnimSet.removeAllListeners();
            secondLoopRunAnimSet.end();
            secondLoopRunAnimSet.cancel();

            secondLoopRunAnimSet = new AnimatorSet();
        }
        //Reset the polylines
        if (foregroundPolyline != null) foregroundPolyline.remove();
        if (backgroundPolyline != null) backgroundPolyline.remove();


        PolylineOptions optionsBackground = new PolylineOptions().add(bangaloreRoute2.get(0)).color(GREY).width(5);
        backgroundPolyline = googleMap.addPolyline(optionsBackground);

        optionsForeground = new PolylineOptions().add(bangaloreRoute2.get(0)).color(Color.BLUE).width(5);
        foregroundPolyline = googleMap.addPolyline(optionsForeground);

        final ValueAnimator percentageCompletion = ValueAnimator.ofInt(0, 100);
        percentageCompletion.setDuration(2000);
        percentageCompletion.setInterpolator(new DecelerateInterpolator());
        percentageCompletion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                List<LatLng> foregroundPoints = backgroundPolyline.getPoints();

                int percentageValue = (int) animation.getAnimatedValue();
                int pointcount = foregroundPoints.size();
                int countTobeRemoved = (int) (pointcount * (percentageValue / 100.0f));
                List<LatLng> subListTobeRemoved = foregroundPoints.subList(0, countTobeRemoved);
                subListTobeRemoved.clear();

                foregroundPolyline.setPoints(foregroundPoints);
            }
        });
        percentageCompletion.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                foregroundPolyline.setColor(GREY);
                foregroundPolyline.setPoints(backgroundPolyline.getPoints());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), GREY, Color.BLUE);
        colorAnimation.setInterpolator(new AccelerateInterpolator());
        colorAnimation.setDuration(600); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                foregroundPolyline.setColor((int) animator.getAnimatedValue());
            }

        });

        ObjectAnimator foregroundRouteAnimator = ObjectAnimator.ofObject(this, "routeIncreaseForward", new RouteEvaluator(), bangaloreRoute2.toArray());
        foregroundRouteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        foregroundRouteAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                backgroundPolyline.setPoints(foregroundPolyline.getPoints());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        foregroundRouteAnimator.setDuration(1600);
//        foregroundRouteAnimator.start();

        firstRunAnimSet.playSequentially(foregroundRouteAnimator,
                percentageCompletion);
        firstRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        secondLoopRunAnimSet.playSequentially(colorAnimation,
                percentageCompletion);
        secondLoopRunAnimSet.setStartDelay(200);

        secondLoopRunAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                secondLoopRunAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        firstRunAnimSet.start();
    }


    public void setRouteIncreaseForward(LatLng endLatLng) {
        List<LatLng> foregroundPoints = foregroundPolyline.getPoints();
        foregroundPoints.add(endLatLng);
        foregroundPolyline.setPoints(foregroundPoints);
    }

}