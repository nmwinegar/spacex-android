package com.nickwinegar.spacexdemo.ui.launch;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.ref.WeakReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.view.View.GONE;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.anyOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LaunchDetailActivityTest {
    private ViewVisibilityIdleResource idleResource;

    @Rule
    public IntentsTestRule<LaunchDetailActivity> activityTestRule = new IntentsTestRule<LaunchDetailActivity>(LaunchDetailActivity.class) {

        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent intent = new Intent(targetContext, LaunchDetailActivity.class);
            int testFlightNumber = 57;
            intent.putExtra(LaunchDetailActivity.FLIGHT_NUMBER, testFlightNumber);
            return intent;
        }
    };

    @Before
    public void testInit() {
        // Wait for progress bar to no longer be visible
        // our activity with data has now loaded
        idleResource = new ViewVisibilityIdleResource(activityTestRule.getActivity().findViewById(R.id.launch_detail_progressbar), GONE);
        IdlingRegistry.getInstance().register(idleResource);
    }

    @After
    public void testCleanup() {
        IdlingRegistry.getInstance().unregister(idleResource);
    }

    @Test
    public void launchDetailActivityTest() {
        activityTestRule.getActivity().findViewById(R.id.launch_detail_progressbar);
        // get relevant UI elements to test
        ViewInteraction actionBar = onView(withId(R.id.app_bar));
        ViewInteraction detailContainer = onView(withId(R.id.launch_detail_container));
        ViewInteraction fab = onView(withId(R.id.play_video_fab));

        // Validate they are all displayed
        actionBar.check(matches(isDisplayed()));
        detailContainer.check(matches(isDisplayed()));
        fab.check(matches(isDisplayed()));
    }

    @Test
    public void launchDetail_launchInfoIsDisplayed() {
        // get relevant UI elements to test
        ViewInteraction launchHeader = onView(withId(R.id.detail_header_textview));
        ViewInteraction launchSuccess = onView(withId(R.id.launch_success_header));
        ViewInteraction launchDescription = onView(withId(R.id.launch_description));

        // Validate they are all displayed with text
        launchHeader.check(matches(isDisplayed()));
        launchSuccess.check(matches(isDisplayed()));
        launchDescription.check(matches(isDisplayed()));
        launchHeader.check(matches(withText(any(String.class))));
        launchSuccess.check(matches(anyOf(withText("Mission Success"), withText("Mission Failure"))));
        launchDescription.check(matches(withText(any(String.class))));
    }

    @Test
    public void launchDetail_locationDetailsAreDisplayed() {
        // get relevant UI elements to test
        ViewInteraction location = onView(withId(R.id.launch_site_location));
        ViewInteraction locationName = onView(withId(R.id.launch_site_full_name));

        // Validate they are all displayed with text
        location.check(matches(isDisplayed()));
        locationName.check(matches(isDisplayed()));
        location.check(matches(withText(any(String.class))));
        locationName.check(matches(withText(any(String.class))));
    }

    @Test
    public void launchDetail_rocketDetailsAreDisplayed() {
        // get relevant UI elements to test
        ViewInteraction cores = onView(withId(R.id.first_stage_cores));
        ViewInteraction payloads = onView(withId(R.id.second_stage_payloads));

        // Validate they are all displayed with child elements
        cores.check(matches(isDisplayed()));
        payloads.check(matches(isDisplayed()));
        cores.check(matches(hasMinimumChildCount(1)));
        payloads.check(matches(hasMinimumChildCount(1)));
    }

    public class ViewVisibilityIdleResource implements IdlingResource {
        private WeakReference<View> view;
        private int desiredVisibility;
        private ResourceCallback callback;

        ViewVisibilityIdleResource(View view, int desiredVisibility) {
            this.view = new WeakReference<>(view);
            this.desiredVisibility = desiredVisibility;
        }

        @Override
        public String getName() {
            return ViewVisibilityIdleResource.class.getName();
        }

        @Override
        public boolean isIdleNow() {
            if (view.get().getVisibility() == desiredVisibility && callback != null)
                callback.onTransitionToIdle();
            return view.get().getVisibility() == desiredVisibility;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.callback = callback;
        }
    }

    // Testing the external intents to YouTube or Maps would sometimes cause tests to hang
    // Commented them out until issues resolved
//    @Test
//    public void launchDetail_fabLaunchesVideoIntent() {
//        Intents.init();
//        // get fab UI element
//        ViewInteraction fab = onView(withId(R.id.play_video_fab));
//
//        // click play video fab
//        fab.perform(click());
//        // mock result intent
//        intending(not(isInternal()))
//                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
//
//        // assert intent is fired
//        intended(allOf(hasAction(ACTION_VIEW),
//                hasData(anyOf(hasHost("www.youtube.com"), hasScheme("vnd.youtube")))));
//        Intents.release();
//    }
//
//    @Test
//    public void launchDetail_locationClickLaunchesMapsIntent() {
//        Intents.init();
//        // get fab UI element
//        ViewInteraction locationLayout = onView(withId(R.id.location_layout));
//
//        // click play video fab
//        locationLayout.perform(click());
//        // mock result intent
//        intending(not(isInternal()))
//                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
//
//        // assert intent is fired
//        intended(allOf(hasAction(ACTION_VIEW),
//                hasData(hasScheme("geo"))));
//        Intents.release();
//    }
}
