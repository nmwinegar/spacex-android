package com.nickwinegar.spacexdemo.ui.launch;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LaunchListActivityTest {

    @Rule
    public IntentsTestRule<LaunchListActivity> activityTestRule = new IntentsTestRule<>(LaunchListActivity.class);

    @Test
    public void launchListActivityTest() {
        // Launch list displays on activity start
        ViewInteraction recyclerView = onView(withId(R.id.launch_list));
        recyclerView.check(matches(isDisplayed()));
        recyclerView.check(matches(hasMinimumChildCount(1)));
    }

    @Test
    public void launchDetailActivityOnClick() {
        // Click an item in the recyclerView to launch detail view
        onView(withId(R.id.launch_list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        // After click, intent is sent to launch detail view
        intended(allOf(toPackage("com.nickwinegar.spacexdemo"),
                hasComponent(LaunchDetailActivity.class.getName())));
    }
}
