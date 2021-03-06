package com.cmt.criminalintent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author ChenMingTao email:cmt96@foxmail.com
 * @create 2021-04-22-16:27
 */
public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private LinearLayout mNoCrimeLinearLayout;
    private ImageView mAddFirstCrimeImageView;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNoCrimeLinearLayout = view.findViewById(R.id.no_crime_view);

        updateUI();

        mAddFirstCrimeImageView = view.findViewById(R.id.add_first_crime);
        mAddFirstCrimeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        if (mSubtitleVisible) {
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            int crimeCount = crimeLab.getCrimes().size();
            String subtitle = getString(R.string.subtitle_format, crimeCount);

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setSubtitle(subtitle);
        } else {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setSubtitle(null);
        }
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimes.size() == 0) {
            mCrimeRecyclerView.setVisibility(View.GONE);
            mNoCrimeLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mNoCrimeLinearLayout.setVisibility(View.GONE);
        }

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback());
        itemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);
        updateSubtitle();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolvedImageView = itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat mformat = new SimpleDateFormat("yyyy???MM???dd??? HH???mm???ss???");
            mDateTextView.setText(mformat.format(mCrime.getDate()));
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            this.mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
                holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    }

    private class ItemTouchCallback extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            // ????????????
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            // ????????????
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = viewHolder.getAdapterPosition();
//            if (fromPosition < toPosition) {
//                //???????????????????????? item ?????????????????????
//                for (int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(mUserBookShelfResponses, i, i + 1);
//                }
//            } else {
//                for (int i = fromPosition; i > toPosition; i--) {
//                    Collections.swap(mUserBookShelfResponses, i, i - 1);
//                }
//            }
//            mAdapter.notifyItemMoved(fromPosition, toPosition);
//            return true;
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            //?????????????????? item ?????????
//            int position = viewHolder.getAdapterPosition();
//            final UserBookShelfResponse bookShelfResponse = mUserBookShelfResponses.get(position);
//            bookShelfResponse.setIndex(position);
//            //????????????????????????????????????????????????????????????
//            queue.add(bookShelfResponse);
//            //????????????????????? item ???????????????????????????
//            //???????????????????????????????????????????????????????????????????????????
//            mUserBookShelfResponses.remove(position);
//            mBookShelfAdapter.notifyItemRemoved(position);
        }

        //????????????
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //??????????????? Item ?????????????????????????????????????????????????????????
                final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

//        //???????????????????????????
//        //?????????????????????????????????
//        @Override
//        public void clearView(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder) {
//            super.clearView(recyclerView, viewHolder);
//            if (!queue.isEmpty()) {
//                //?????????????????????????????????????????????????????? item
//                Snackbar.make(((BaseActivity) getActivity()).getToolbar(), R.string.delete_bookshelf_success, Snackbar.LENGTH_LONG).setAction(R.string.repeal, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //SnackBar ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                        final UserBookShelfResponse bookShelfResponse = (UserBookShelfResponse) queue.remove();
//                        //?????? Adapter                        mBookShelfAdapter.notifyItemInserted(bookShelfResponse.getIndex());
//                        mUserBookShelfResponses.add(bookShelfResponse.getIndex(), bookShelfResponse);
//                        //??????????????????????????? bug??????????????????item??????????????????????????????
//                        //????????? recyclerView ??????????????????????????????bug
//                        if (bookShelfResponse.getIndex() == 0) {
//                            mRecyclerView.smoothScrollToPosition(0);
//                        }
//                    }
//                }).setCallback(new Snackbar.Callback() {
//                    //????????????????????????????????????????????? SnackBar ???????????????
//                    //SnackBar ?????????????????????????????????????????????????????????????????????
//                    @Override
//                    public void onDismissed(Snackbar snackbar, int event) {
//                        super.onDismissed(snackbar, event);
//                        //event ?????????????????????????????????????????????3???
//                        //?????????????????????????????????????????? item SnackBar ??????????????? SnackBar ?????????
//                        //??????
//                        if (event != DISMISS_EVENT_ACTION) {
//                            final UserBookShelfResponse bookShelfResponse = (UserBookShelfResponse) queue.remove();
//                            mLibraryPresenter.setBookShelf(BaseApplication.getUserId(), BaseApplication.getUserPassword(),
//                                    bookShelfResponse.getName(), bookShelfResponse.getRemark(), bookShelfResponse.getID(), 1);
//                        }
//                    }
//                }).show();
//            }
//        }

        //????????????????????????
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    }

}