/*
 * MIT License
 *
 * Copyright (c) 2016 Alibaba Group
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xk.nblm.example;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import com.xk.nblm.R;
import com.xk.nblm.example.data.ExampleDataProvider;
import com.xk.nblm.vdrag.animator.DraggableItemAnimator;
import com.xk.nblm.vdrag.animator.GeneralItemAnimator;
import com.xk.nblm.vdrag.decoration.ItemShadowDecorator;
import com.xk.nblm.vdrag.draggble.DraggableItemAdapter;
import com.xk.nblm.vdrag.draggble.ItemDraggableRange;
import com.xk.nblm.vdrag.draggble.RecyclerViewDragDropManager;
import com.xk.nblm.vdrag.utils.AbstractDraggableItemViewHolder;
import com.xk.nblm.vdrag.utils.WrapperAdapterUtils;
import com.xk.nblm.vlayout.DelegateAdapter;
import com.xk.nblm.vlayout.LayoutHelper;
import com.xk.nblm.vlayout.VirtualLayoutManager;
import com.xk.nblm.vlayout.VirtualLayoutManager.LayoutParams;
import com.xk.nblm.vlayout.extend.LayoutManagerCanScrollListener;
import com.xk.nblm.vlayout.layout.OnePlusNLayoutHelper;
import com.xk.nblm.vlayout.layout.SingleLayoutHelper;
import com.xk.nblm.vlayout.layout.XLayoutHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author villadora
 */
public class NBPlusLayoutActivity extends Activity {

    private static final boolean SINGLE_LAYOUT = true;
    private static final boolean ONEN_LAYOUT = true;

//    private TextView mFirstText;
//    private TextView mLastText;
//    private TextView mCountText;
//    private TextView mTotalOffsetText;

    RecyclerView recyclerView;
    /**
     *    以下为advanced_recycler
     */
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerView.Adapter mWrappedAdapter;

    private static int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nb_activity);
//        mFirstText = findViewById(R.id.first);
//        mLastText = findViewById(R.id.last);
//        mCountText = findViewById(R.id.count);
//        mTotalOffsetText = findViewById(R.id.total_offset);

        recyclerView = findViewById(R.id.main_view);
        final VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {}
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
//                mFirstText.setText("First: " + layoutManager.findFirstVisibleItemPosition());
//                mLastText.setText("Existing: " + MainViewHolder.existing + " Created: " + MainViewHolder.createdTimes);
//                mCountText.setText("Count: " + recyclerView.getChildCount());
//                mTotalOffsetText.setText("Total Offset: " + layoutManager.getOffsetToStart());
            }
        });

        recyclerView.setLayoutManager(layoutManager);

        // layoutManager.setReverseLayout(true);

        final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

        recyclerView.setRecycledViewPool(viewPool);

        // recyclerView.addItemDecoration(itemDecoration);

        viewPool.setMaxRecycledViews(0, 20);

        layoutManager.setRecycleOffset(300);

        layoutManager.setLayoutManagerCanScrollListener(new LayoutManagerCanScrollListener() {
            @Override
            public boolean canScrollVertically() {
                Log.i("vlayout", "canScrollVertically: ");
                return true;
            }

            @Override
            public boolean canScrollHorizontally() {
                Log.i("vlayout", "canScrollHorizontally: ");
                return true;
            }
        });

        final DelegateAdapter delegateAdapter = new DelegateAdapter(layoutManager, true);


        // ============================================

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);

        NBAdapter nbAdapter = new NBAdapter(new ExampleDataProvider());
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(nbAdapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        recyclerView.setAdapter(mWrappedAdapter);

        recyclerView.setItemAnimator(animator);
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z1)));
        }

        mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);
        mRecyclerViewDragDropManager.setItemMoveMode(RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP);

        // ============================================

        final List<DelegateAdapter.Adapter> adapters = new LinkedList<>();


        if (ONEN_LAYOUT) {
            OnePlusNLayoutHelper helper = new OnePlusNLayoutHelper();
            helper.setBgColor(0xff876384);
            helper.setAspectRatio(4.0f);
            helper.setColWeights(new float[]{100/3f, 200/3f});
            helper.setMargin(10, 20, 10, 20);
            helper.setPadding(10, 10, 10, 10);
            adapters.add(new SubAdapter(this, helper, 2));
            OnePlusNLayoutHelper helper2 = new OnePlusNLayoutHelper();
            helper2.setBgColor(0xff234555);
            helper2.setAspectRatio(4.0f);
            helper2.setColWeights(new float[]{200/3f, 100/3f});
            helper2.setMargin(10, 20, 10, 20);
            helper2.setPadding(10, 10, 10, 10);
            adapters.add(new SubAdapter(this, helper2, 2));
        }

        if (ONEN_LAYOUT) {
            XLayoutHelper helper = new XLayoutHelper();
            helper.setBgColor(0xff87e543);
            helper.setAspectRatio(1.8f);
            helper.setColWeights(new float[]{100f/3, 100f/3, 200f/3});
            helper.setMargin(10, 20, 10, 20);
            helper.setPadding(10, 10, 10, 10);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            adapters.add(new SubAdapter(this, helper, 3, lp) {
                @Override
                public void onBindViewHolder(MainViewHolder holder, int position) {
                    super.onBindViewHolder(holder, position);
                    LayoutParams lp = (LayoutParams) holder.itemView.getLayoutParams();
                    if (position == 0 || position ==1) {
                        lp.rightMargin = 1;
                    }
                }
            });

            OnePlusNLayoutHelper helper2 = new OnePlusNLayoutHelper();
            helper2.setBgColor(0xff87e543);
            helper2.setAspectRatio(1.8f);
            helper2.setColWeights(new float[]{200f/3, 100f/3, 100f/3});
            helper2.setMargin(10, 20, 10, 20);
            helper2.setPadding(10, 10, 10, 10);
            LayoutParams lp2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            adapters.add(new SubAdapter(this, helper2, 3, lp2) {
                @Override
                public void onBindViewHolder(MainViewHolder holder, int position) {
                    super.onBindViewHolder(holder, position);
                    LayoutParams lp = (LayoutParams) holder.itemView.getLayoutParams();
                    if (position == 0 || position ==1) {
                        lp.rightMargin = 1;
                    }
                }
            });

            OnePlusNLayoutHelper helper3 = new OnePlusNLayoutHelper();
            helper3.setBgColor(0xff876384);
            helper3.setAspectRatio(1.8f);
            helper3.setMargin(0, 10, 0, 10);
            adapters.add(new SubAdapter(this, helper3, 1));

        }

        if (ONEN_LAYOUT) {
            OnePlusNLayoutHelper helper = new OnePlusNLayoutHelper();
            helper.setBgColor(0xff876384);
            helper.setAspectRatio(4.0f);
            helper.setColWeights(new float[]{100/3f, 200/3f});
            helper.setMargin(10, 20, 10, 20);
            helper.setPadding(10, 10, 10, 10);
            adapters.add(new SubAdapter(this, helper, 2));
            OnePlusNLayoutHelper helper2 = new OnePlusNLayoutHelper();
            helper2.setBgColor(0xff234555);
            helper2.setAspectRatio(4.0f);
            helper2.setColWeights(new float[]{200/3f, 100/3f});
            helper2.setMargin(10, 20, 10, 20);
            helper2.setPadding(10, 10, 10, 10);
            adapters.add(new SubAdapter(this, helper2, 2));
        }

        delegateAdapter.setAdapters(adapters);


        final Handler mainHandler = new Handler(Looper.getMainLooper());

        setListenerToRootView();
    }

    boolean isOpened = false;

    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                    if (isOpened == false) {
                        //Do two things, make the view top visible and the editText smaller
                    }
                    isOpened = true;
                } else if (isOpened == true) {
                    isOpened = false;
                    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_view);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }


    static class SubAdapter extends DelegateAdapter.Adapter<MainViewHolder>  implements DraggableItemAdapter<MainViewHolder> {

        private Context mContext;

        private LayoutHelper mLayoutHelper;


        private LayoutParams mLayoutParams;
        private int mCount = 0;


        public SubAdapter(Context context, LayoutHelper layoutHelper, int count) {
            this(context, layoutHelper, count, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        }

        public SubAdapter(Context context, LayoutHelper layoutHelper, int count, @NonNull LayoutParams layoutParams) {
            this.mContext = context;
            this.mLayoutHelper = layoutHelper;
            this.mCount = count;
            this.mLayoutParams = layoutParams;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return mLayoutHelper;
        }

        @Override
        public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(MainViewHolder holder, int position) {
            // only vertical
            holder.itemView.setLayoutParams(new LayoutParams(mLayoutParams));
        }


        @Override
        protected void onBindViewHolderWithOffset(MainViewHolder holder, int position, int offsetTotal) {
            ((TextView) holder.itemView.findViewById(R.id.title)).setText(Integer.toString(offsetTotal));

        }

        @Override
        public int getItemCount() {
            return mCount;
        }

        @Override
        public boolean onCheckCanStartDrag(@NonNull MainViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Nullable
        @Override
        public ItemDraggableRange onGetItemDraggableRange(@NonNull MainViewHolder holder, int position) {
            return null;
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            Log.d("khw", "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

            if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
//                mProvider.moveItem(fromPosition, toPosition);
            } else {
//                mProvider.swapItem(fromPosition, toPosition);
            }
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }

        @Override
        public void onItemDragStarted(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            notifyDataSetChanged();
        }
    }

    static class MainViewHolder extends AbstractDraggableItemViewHolder {

        public static volatile int existing = 0;
        public static int createdTimes = 0;

        public MainViewHolder(View itemView) {
            super(itemView);
            createdTimes++;
            existing++;
        }

        @Override
        protected void finalize() throws Throwable {
            existing--;
            super.finalize();
        }
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }


        super.onDestroy();
    }
}
