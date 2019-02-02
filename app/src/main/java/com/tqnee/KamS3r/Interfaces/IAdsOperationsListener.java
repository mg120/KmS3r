package com.tqnee.KamS3r.Interfaces;

/**
 * Created by ramzy on 9/12/2017.
 */

public interface IAdsOperationsListener {
    void onFavouriteClicked(int position);

    void onMessageClicked(int position);

    void onShareClicked(int position);
    void onItemClicked(int position);

    void onAnswerClicked(int position);

    void onOptionMenuClicked(int position);


    void onImageClicked(int position);

    void onUserClicked(int position);
}
