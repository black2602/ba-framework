package com.angel.black.baframework;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.InputDevice;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.angel.black.baframework.espresso.RecyclerViewMatcher;
import com.angel.black.baframework.espresso.ToastMatcher;
import com.blackangel.baframework.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static android.view.MotionEvent.BUTTON_PRIMARY;
import static com.angel.black.baframework.BaseTestEspresso.EspressoTestsMatchers.withDrawable;
import static com.angel.black.baframework.espresso.LongListMatchers.withItemContent;
import static junit.framework.Assert.fail;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Finger-kjh on 2017-12-08.
 */

@RunWith(AndroidJUnit4.class)
public abstract class BaseTestEspresso {
    protected static final int LAUNCH_TIMEOUT = 10;
    protected static final long DEFAULT_TIMEOUT = 5000;

    /**
     * 비동기로 동작할 때 테스트의 성공 여부. (테스트 메소드의 마지막에 이 값을 검사하여 최종 테스트 통과 판별한다.)
     */
    protected boolean mIsTestSuccAsync;

    @Before
    public void setup() {
        log("setup");
        mIsTestSuccAsync = false;
    }

    public void waitTime(int sec) {
        SystemClock.sleep(sec * 1000);
    }

    public Fragment findViewPagerFragment(ActivityTestRule<? extends AppCompatActivity> activityTestRule, int viewPageId, int pageIdx) {
        return activityTestRule.getActivity().getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + viewPageId + ":" + pageIdx);
    }

    public Fragment findFragment(ActivityTestRule<? extends AppCompatActivity> activityTestRule, Class<? extends Fragment> fragmentClass) {
        return activityTestRule.getActivity().getSupportFragmentManager()
                .findFragmentByTag(fragmentClass.getSimpleName());
    }

    public Fragment findChildFragment(ActivityTestRule<? extends AppCompatActivity> activityTestRule, Class<? extends Fragment> parentFragmentClass,
                                      Class<? extends Fragment> childFragmentClass) {
        return activityTestRule.getActivity().getSupportFragmentManager()
                .findFragmentByTag(parentFragmentClass.getSimpleName())
                .getChildFragmentManager().findFragmentByTag(childFragmentClass.getSimpleName());
    }

    public String getResourceString(int id) {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        return targetContext.getResources().getString(id);
    }

    public void checkDisplayViewById(int viewId) {
        onView(withId(viewId)).check(matches(isDisplayed()));
    }

    /**
     * 특정 뷰가 존재는 하지만, 보여지고 있지 않은지 검증한다.
     */
    public void checkNotDisplayViewById(int viewId) {
        onView(withId(viewId)).check(matches(not(isDisplayed())));
    }

    public void checkDisplayViewByText(String text) {
        onView(withText(text)).check(matches(isDisplayed()));
    }

    public void checkDisplayViewByTextDescendantOf(int ascendentViewId, int textResId) {
        onView(allOf(withText(getResourceString(textResId)),
                isDescendantOfA(withId(ascendentViewId)))).check(matches(isDisplayed()));
    }

    public void checkDisplayViewByText(int textResId) {
        onView(withText(textResId)).check(matches(isDisplayed()));
    }

    public void checkDisplayView(Matcher<View> viewMatcher) {
        onView(viewMatcher).check(matches(isDisplayed()));
    }

    public void checkDisplayListItemSize(int adapterViewId, int size) {
        onView(withId(adapterViewId)).check(matches(withListSize(size)));
    }

    /**
     * 특정 id 의 뷰가 존재하지 않는지 검증한다.
     * @param viewId
     */
    public void checkNotExistViewById(int viewId) {
        try {
            onView(withId(viewId)).check(ViewAssertions.doesNotExist());
        } catch (NoMatchingViewException e) {
            // 성공
        }
    }


    /**
     * 특정 텍스트 리소스를 가지는 뷰가 존재하지 않는지 검증한다.
     * @param textResId
     */
    public void checkNotExistViewByText(int textResId) {
        try {
            onView(withText(textResId)).check(ViewAssertions.doesNotExist());
        } catch (NoMatchingViewException e) {
            // 성공
        }
    }

    /**
     * 특정 텍스트 리소스를 가지는 뷰가 존재하지 않는지 검증한다.
     * @param text
     */
    public void checkNotExistViewByText(String text) {
        try {
            onView(withText(text)).check(ViewAssertions.doesNotExist());
        } catch (NoMatchingViewException e) {
            // 성공

        }
    }

    public void checkNotVisibleViewByText(String text) {
        onView(withText(text)).check(matches(not(ViewMatchers.isCompletelyDisplayed())));
    }

    public void checkDisplayByDesc(String desc) {
        onView(withContentDescription(desc)).check(matches(isDisplayed()));
    }

    public void checkToast(int txtResId) {
        String txt = getResourceString(txtResId);
        checkToast(txt);
    }

    public void checkToast(String txt) {
        onView(withText(txt))
                .inRoot(new ToastMatcher(txt))
                .check(matches(isDisplayed()));
    }

    public void checkDisplayViewChecked(int checkableViewId, boolean isChecked) {
        if(isChecked) {
            onView(withId(checkableViewId)).check(matches(isChecked()));
        } else {
            onView(withId(checkableViewId)).check(matches(not(isChecked())));
        }
    }

    public void checkDisplayViewEnabled(int enableViewId, boolean isEnabled) {
        if(isEnabled) {
            onView(withId(enableViewId)).check(matches(ViewMatchers.isEnabled()));
        } else {
            onView(withId(enableViewId)).check(matches(not(ViewMatchers.isEnabled())));
        }
    }

    public void checkDisplayViewEnabledDescendantOf(int ascendentViewId, int enableViewId, boolean isEnable) {
        if(isEnable) {
            onView(allOf(withId(enableViewId), isDescendantOfA(withId(ascendentViewId)))).check(matches(ViewMatchers.isEnabled()));
        } else {
            onView(allOf(withId(enableViewId), isDescendantOfA(withId(ascendentViewId)))).check(matches(not(ViewMatchers.isEnabled())));
        }
    }

    protected void checkDisplayImageViewResource(int imageViewId, int imgResId) {
        onView(withId(imageViewId)).check(matches(withDrawable(imgResId)));
    }

    protected void checkDisplayImageViewResource(int imgResId) {
        onView(withDrawable(imgResId)).check(matches(isDisplayed()));
    }

    protected void checkDisplayViewByTextContains(int txtViewId, String text) {
        onView(withId(txtViewId)).check(matches(withText(containsString(text))));
    }

    public void checkDisplayError(int editTextId, int errTextResId) {
        checkDisplayError(editTextId, getResourceString(errTextResId));
    }

    public void checkDisplayError(int editTextId, String errText) {
        onView(withId(editTextId)).check(matches(hasErrorText(errText)));
    }

    public void checkDisplayErrorInInputLayout(int inputLayoutId, int errTextResId) {
        checkDisplayErrorInInputLayout(inputLayoutId, getResourceString(errTextResId));
    }

    public void checkDisplayErrorInInputLayout(int inputLayoutId, String errText) {
        onView(withId(inputLayoutId)).check(matches(withErrorInInputLayout(errText)));
    }

    public void checkDisplayErrorDescendantOf(int ascendentViewId, int editTextId, int errTextResId) {
        checkDisplayErrorDescendantOf(ascendentViewId, editTextId, getResourceString(errTextResId));
    }

    public void checkDisplayErrorInInputLayoutDescendantOf(int ascendentViewId, int inputLayoutId, int errTextResId) {
        checkDisplayErrorInInputLayoutDescendantOf(ascendentViewId, inputLayoutId, getResourceString(errTextResId));
    }

    public void checkDisplayErrorInInputLayoutDescendantOf(int ascendentViewId, int inputLayoutId, String errText) {
        onView(allOf(withId(inputLayoutId), isDescendantOfA(withId(ascendentViewId)))).check(matches(withErrorInInputLayout(errText)));
    }

    public void checkDisplayErrorDescendantOf(int ascendentViewId, int editTextId, String errText) {
        onView(allOf(withId(editTextId), isDescendantOfA(withId(ascendentViewId)))).check(matches(hasErrorText(errText)));
    }

    public void performClickByText(String text) {
        onView(withText(text)).perform(click());
    }

    public void performClickByTextDescendantOf(int ascendentViewId, String text) {
        onView(allOf(withText(text), isDescendantOfA(withId(ascendentViewId)))).perform(click());
    }

    public void performClickByIdDescendantOf(int ascendentViewId, int viewId) {
        onView(allOf(withId(viewId), isDescendantOfA(withId(ascendentViewId)))).perform(click());
    }

    public void performClickByTextDescendantOf(int ascendentViewId, int textResId) {
        onView(allOf(withText(getResourceString(textResId)), isDescendantOfA(withId(ascendentViewId)))).perform(click());
    }

    public void performClickMenu(int menuId, int menuTextRes) {
        onView(withMenuIdOrText(menuId, menuTextRes)).perform(click());
    }

    public void checkDisplayedMenu(int menuId, int menuTextRes) {
        onView(withMenuIdOrText(menuId, menuTextRes)).check(matches(isDisplayed()));
    }
    /**
     * ascendentContentDesc 를 contentDescription 으로 가지고 있는 조상뷰 하위에 있는 뷰 중에 text 가 셋팅되어있는
     * 뷰를 찾아서 클릭한다.
     *
     * @param ascendentContentDesc
     * @param text
     */
    public void performClickByTextDescendentOfContentDescAscendent(String ascendentContentDesc, String text) {
        onView(allOf(withText(text), isDescendantOfA(withContentDescription(ascendentContentDesc)))).perform(click());
    }

    public void performClickByContentDescChildOf(int parentViewId, String contentDesc) {
        onView(allOf(withContentDescription(contentDesc), withParent(withId(parentViewId)))).perform(click());
    }

    public void performClickByText(@StringRes int textResId) {
        onView(withText(textResId)).perform(click());
    }

    public void performClickByDesc(String desc) {
        onView(withContentDescription(desc)).perform(click());
    }

    public void performClickById(@IdRes int viewId) {
        onView(withId(viewId)).perform(click());
    }

    public void performClickSoftKeyboardImeButton(int viewId) {
        onView(withId(viewId)).perform(pressImeActionButton());
    }

    public void performClickByIdWithScrollDown(int viewId) {
        onView(withId(viewId)).perform(scrollTo()).perform(click());
    }

    protected void performClickByIdFromFar(int viewId, int x, int y) {
        onView(withId(viewId)).perform(clickXY(x, y));
    }

    public void checkViewDisplayedWithScrollDown(int viewId) {
        onView(withId(viewId)).perform(scrollTo()).check(matches(isDisplayed()));
    }

//    public void checkTextDisplayedInView(int viewId) {
//        onView(withId(viewId)).check().perform(scrollTo()).check(matches(isDisplayed()));
//    }

    public void scrollDown(int viewId) {
        onView(withId(viewId)).perform(scrollTo());
    }

    public void scrollDownInRecyclerView(int recyclerViewId, int itemCount) {
        onView(withId(recyclerViewId))
                .perform(RecyclerViewActions.scrollToPosition(itemCount - 1));
    }

    public void scrollDownInRecyclerViewDescendantOfWithContentDesc(String ascendantViewContentDesc, int recyclerViewId, int itemCount) {
        onView(allOf(withId(recyclerViewId), isDescendantOfA(withContentDescription(ascendantViewContentDesc))))
                .perform(RecyclerViewActions.scrollToPosition(itemCount - 1));
//                .perform(swipeUp());
    }

    public void swipeUpScrollDownInRecyclerViewDescendantOfWithContentDesc(String ascendantViewContentDesc, int recyclerViewId, int swipeCount) {
        ViewAction[] actions = new ViewAction[swipeCount];

        for(int i=0; i < swipeCount; i++)
            actions[i] = swipeUp();

        onView(allOf(withId(recyclerViewId), isDescendantOfA(withContentDescription(ascendantViewContentDesc))))
                .perform(actions);
    }

    /**
     * 주어진 contentDescription 의 특정 조상뷰안에 있는 리사이클러뷰를 찾는다.
     * @param ascendantViewContentDesc
     * @param recyclerViewId
     * @return
     */
    public Matcher<View> withRecyclerViewIn(String ascendantViewContentDesc, int recyclerViewId) {
        return allOf(withId(recyclerViewId), isDescendantOfA(withContentDescription(ascendantViewContentDesc)));
    }

    /**
     * 특정 조상뷰 아래에 있는 RecyclerView 안의 position 번째의 아이템이 보이고 있는지 체크한다.
     * (RecyclerView 는 AdapterView 가 아님!! inAdapterView 쓰면 안됨!)
     * @param ascendantViewContentDesc
     * @param recyclerViewId
     * @param position
     */
    public void checkDisplayedItemInRecyclerViewDescendentOfWithContentDesc(String ascendantViewContentDesc, int recyclerViewId, int position) {
        onView(withRecyclerViewIn(ascendantViewContentDesc, recyclerViewId))
                .check(matches(atPositionInRecyclerView(position, is(isDisplayed()))));
    }

    /**
     * 특정 조상뷰 아래에 있는 RecyclerView 안의 position 번째의 아이템 안의 특정 자식뷰가 보이고 있는지 조사한다.
     * @param ascendantViewContentDesc
     * @param recyclerViewId
     * @param position
     * @param childViewId
     */
    public void checkDisplayedChildViewInItemInRecyclerViewDescendentOfWithContentDesc(String ascendantViewContentDesc, int recyclerViewId, int position, int childViewId) {
        onView(withRecyclerViewIn(ascendantViewContentDesc, recyclerViewId))
                .check(matches(atPositionInRecyclerView(position, withId(childViewId))));
    }

    public void checkContainTextInRecyclerViewDescendentOfWithContentDesc(String ascendantViewContentDesc, int recyclerViewId, int position, String text) {
        onView(withRecyclerViewIn(ascendantViewContentDesc, recyclerViewId))
                .check(matches(atPositionInRecyclerView(position, withText(containsString(text)))));
    }

    public void performClickListItem(int adapterViewId, int idx) {
        onData(anything()).inAdapterView(withId(adapterViewId)).atPosition(idx).perform(click());
    }

    public void checkListViewItemChecked(int adapterViewId, int checkViewId, int idx, boolean isChecked) {
        if(isChecked) {
            onData(anything()).inAdapterView(withId(adapterViewId)).atPosition(idx).onChildView(
                    withId(checkViewId)).check(matches(isChecked()));
        } else {
            onData(anything()).inAdapterView(withId(adapterViewId)).atPosition(idx).onChildView(
                    withId(checkViewId)).check(matches(not(isChecked())));
        }
    }

    public void performClickExpandableListItem(Class adapterClass, int expandableListViewId, int idx, String childText) {
        onData(withItemContent(childText))
                .inAdapterView(withId(expandableListViewId))
                .perform(click());
    }

    public void log(String s) {
        System.out.println(" Espresso UiTest[" + this.getClass().getSimpleName() + "] : " + s);
    }


    protected void pressBack() {
//        pressBackUnconditionally();
        Espresso.pressBack();
    }

    protected void pressNaviBack() {
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
//        onView(isRoot()).perform(ViewActions.pressMenuKey());
    }

    protected void assertAppClosed(Class<? extends Activity> clazz) {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor activityMonitor = instrumentation.addMonitor(clazz.getName(), null, false);
        Activity activity = instrumentation.waitForMonitorWithTimeout(activityMonitor, 1000);

        pressBack();

        if(activity != null) {
            // do something
            fail();
        }
    }

    /**
     * 만약 주어진 text 의 언어와 시스템의 키보드 언어가 다르다면 잘못입력된다.
     * 이럴때는 performInputChange 메소드를 이용한다.
     *
     * @param editTextViewId
     * @param text
     */
    protected void performInput(int editTextViewId, String text) {
        try {
            onView(withId(editTextViewId)).perform(ViewActions.typeText(text));
        } catch (Exception e) {
            onView(withId(editTextViewId)).perform(ViewActions.replaceText(text));
        }
    }


    protected void performInputChange(int editTextViewId, String text) {
        onView(withId(editTextViewId)).perform(ViewActions.replaceText(text));
    }

    public void performInputChangeDescendantOf(int ascendentViewId, int editTextViewId, String text) {
        onView(allOf(withId(editTextViewId), isDescendantOfA(withId(ascendentViewId)))).perform(ViewActions.replaceText(text));
    }

    public void performInputDescendantOf(int ascendentViewId, int editTextViewId, String text) {
        onView(allOf(withId(editTextViewId), isDescendantOfA(withId(ascendentViewId)))).perform(ViewActions.typeText(text));
    }

    protected void closeSoftKeyboard() {
        Espresso.closeSoftKeyboard();
    }

    public void performClickInRecyclerViewItem(int recyclerViewId, int position) {
        onView(ViewMatchers.withId(recyclerViewId))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    public void performClickInRecyclerViewItemDescendentOf(String aescendentViewContentDesc, int recyclerViewId, int position) {
        onView(withRecyclerViewIn(aescendentViewContentDesc, recyclerViewId))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, click()));
    }

    public void checkDisplayedViewByIdForWaiting(int viewId, int sec) {
        onView(isRoot()).perform(waitId(viewId, TimeUnit.SECONDS.toMillis(sec)));
    }

    /** Perform action of waiting for a specific view id. */
    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    public static class EspressoTestsMatchers {

        /**
         * 특정 drawable 리소스 id 를 가지고 있는 뷰를 찾는다.
         * @param resourceId
         * @return
         */
        public static Matcher<View> withDrawable(final int resourceId) {
            return new DrawableMatcher(resourceId);
        }

        public static Matcher<View> noDrawable() {
            return new DrawableMatcher(-1);
        }
    }

    /**
     * 특정 drawable 리소스 id 를 가지고 있는 뷰를 찾아주는 Matcher
     */
    public static class DrawableMatcher extends TypeSafeMatcher<View> {
        private final int expectedId;

        public DrawableMatcher(int resourceId) {
            super(View.class);
            this.expectedId = resourceId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageView)){
                return false;
            }
            ImageView imageView = (ImageView) target;
            if (expectedId < 0){
                return imageView.getDrawable() == null;
            }
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            if (expectedDrawable == null) {
                return false;
            }
            Bitmap bitmap = getBitmap (imageView.getDrawable());
            Bitmap otherBitmap = getBitmap(expectedDrawable);
            return bitmap.sameAs(otherBitmap);
        }

        private Bitmap getBitmap(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        @Override
        public void describeTo(Description description) {

        }
    }

    /**
     * 특정 뷰에서 x, y만큼 떨어진 곳을 터치하도록 만든다.
     * @param x
     * @param y
     * @return
     */
    public static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER, InputDevice.SOURCE_TOUCHSCREEN, BUTTON_PRIMARY);
    }

    public static Matcher<View> hasTextInputLayoutHintText(final String expectedHintText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence hint = ((TextInputLayout) view).getHint();

                if (hint == null) {
                    return false;
                }

                return expectedHintText.equals(hint.toString());
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();

                if (error == null) {
                    return false;
                }

                return expectedErrorText.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public static Matcher<View> withListSize (final int size) {
        return new TypeSafeMatcher<View> () {
            @Override
            public boolean matchesSafely (final View view) {
                return ((ListView) view).getCount () == size;
            }

            @Override
            public void describeTo (final Description description) {
                description.appendText ("ListView should have " + size + " items");
            }
        };
    }

    public static Matcher<View> withErrorInInputLayout(final Matcher<String> stringMatcher) {
        checkNotNull(stringMatcher);

        return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {
            String actualError = "";

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: ");
                stringMatcher.describeTo(description);
//                description.appendText("But got: " + actualText);
            }

            @Override
            public boolean matchesSafely(TextInputLayout textInputLayout) {
                CharSequence error = textInputLayout.getError();
                if (error != null) {
                    actualError = error.toString();
                    return stringMatcher.matches(actualError);
                }
                return false;
            }
        };
    }

    public static Matcher<View> withErrorInInputLayout(final String string) {
        return withErrorInInputLayout(is(string));
    }

    /**
     * Menu 의 id 혹은 텍스트로 찾아주는 매쳐
     * @param id
     * @param menuText
     * @return
     */
    public static Matcher<View> withMenuIdOrText(@IdRes int id, @StringRes int menuText) {
        Matcher<View> matcher = withId(id);
        try {
            onView(matcher).check(matches(isDisplayed()));
            return matcher;
        } catch (Exception NoMatchingViewException) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            return withText(menuText);
        }
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }

    /**
     * 리사이클러뷰 안의 특정 위치에 있는 아이템을 체크하기 위한 매쳐
     * @param position      특정 아이템 위치
     * @param itemMatcher   뷰 매쳐
     * @return
     */
    public static Matcher<View> atPositionInRecyclerView(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
