/*
 * Copyright 2013 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marshalchen.common.demoofui.listviewanimations;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.marshalchen.common.uiModule.listviewanimations.itemmanipulation.OnDismissCallback;
import com.marshalchen.common.uiModule.listviewanimations.ArrayAdapter;

import com.marshalchen.common.uiModule.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.marshalchen.common.uiModule.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.marshalchen.common.demoofui.R;

public class GoogleCardsActivity extends BaseActivity implements OnDismissCallback {

    private GoogleCardsAdapter mGoogleCardsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_anim_activity_googlecards);

        ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);

        mGoogleCardsAdapter = new GoogleCardsAdapter(this);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mGoogleCardsAdapter, this));
        swingBottomInAnimationAdapter.setInitialDelayMillis(300);
        swingBottomInAnimationAdapter.setAbsListView(listView);

        listView.setAdapter(swingBottomInAnimationAdapter);

        mGoogleCardsAdapter.addAll(getItems());
    }

    private ArrayList<Integer> getItems() {
        ArrayList<Integer> items = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            items.add(i);
        }
        return items;
    }

    @Override
    public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            mGoogleCardsAdapter.remove(position);
        }
    }

    private static class GoogleCardsAdapter extends ArrayAdapter<Integer> {

        private final Context mContext;
        private final LruCache<Integer, Bitmap> mMemoryCache;

        public GoogleCardsAdapter(final Context context) {
            mContext = context;

            final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
            mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(final Integer key, final Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
                }
            };
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            ViewHolder viewHolder;
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.list_anim_activity_googlecards_card, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.activity_googlecards_card_textview);
                view.setTag(viewHolder);

                viewHolder.imageView = (ImageView) view.findViewById(R.id.activity_googlecards_card_imageview);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.textView.setText("This is card " + (getItem(position) + 1));
            setImageView(viewHolder, position);

            return view;
        }

        private void setImageView(final ViewHolder viewHolder, final int position) {
            int imageResId;
            switch (getItem(position) % 5) {
                case 0:
                    imageResId = R.drawable.list_anim_img_nature1;
                    break;
                case 1:
                    imageResId = R.drawable.list_anim_img_nature2;
                    break;
                case 2:
                    imageResId = R.drawable.list_anim_img_nature3;
                    break;
                case 3:
                    imageResId = R.drawable.list_anim_img_nature4;
                    break;
                default:
                    imageResId = R.drawable.list_anim_img_nature5;
            }

            Bitmap bitmap = getBitmapFromMemCache(imageResId);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
                addBitmapToMemoryCache(imageResId, bitmap);
            }
            viewHolder.imageView.setImageBitmap(bitmap);
        }

        private void addBitmapToMemoryCache(final int key, final Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        private Bitmap getBitmapFromMemCache(final int key) {
            return mMemoryCache.get(key);
        }

        private static class ViewHolder {
            TextView textView;
            ImageView imageView;
        }
    }
}
