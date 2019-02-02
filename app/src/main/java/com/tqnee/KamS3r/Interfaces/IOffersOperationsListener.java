package com.tqnee.KamS3r.Interfaces;

/**
 * Created by ramzy on 9/12/2017.
 */

public interface IOffersOperationsListener {
    void onFavouriteClicked(int position);

    void onMessageClicked(int position);

    void onShareClicked(int position);

    void onImageClicked(int position);

    void onUserClicked(int position);

    void onOptionMenuClicked(int position);
}
