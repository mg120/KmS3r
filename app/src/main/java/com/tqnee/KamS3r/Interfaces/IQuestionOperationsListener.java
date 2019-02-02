package com.tqnee.KamS3r.Interfaces;

/**
 * Created by ramzy on 9/12/2017.
 */

public interface IQuestionOperationsListener {
    void onFavouriteClicked(int position);

    void onStatisticalClicked(int position);

    void onMessageClicked(int position);

    void onShareClicked(int position);

    void onAnswerClicked(int position);

    void onOptionMenuClicked(int position);

    void onOffersClicked(int position);

    void onQuestionClicked(int position);

    void onImageClicked(int position);

    void onUserClicked(int position);
}
