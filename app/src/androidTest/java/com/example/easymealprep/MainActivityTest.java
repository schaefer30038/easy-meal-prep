package com.example.easymealprep;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.notNullValue;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkIfButtonExists(){

        Espresso.onView(withId(R.id.loginB_Button)).check(matches(notNullValue()));
        Espresso.onView(withId(R.id.loginB_Button)).perform(click());
        Espresso.onView(withId(R.id.newAccount_Button)).check(matches(notNullValue()));
        Espresso.onView(withId(R.id.newAccount_Button)).perform((click()));

    }

    @Test
    public void editTextWorks(){
        Espresso.onView(withId(R.id.username_EditText)).perform(typeText("Raj"));
        Espresso.onView(withId(R.id.password_EditText)).perform(typeText("qwerty"));

    }

    @Test
    public void textViewExists(){
       // Espresso.onView(withId(R.id.title_TextView)).check(matches(notNullValue()));
       // Espresso.onView(withId(R.id.title_TextView)).perform(click());
        Espresso.onView(withId(R.id.login_TextView)).check(matches(notNullValue()));
        Espresso.onView(withId(R.id.login_TextView)).perform((click()));

    }


}