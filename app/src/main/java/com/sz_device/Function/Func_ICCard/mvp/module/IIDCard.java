package com.sz_device.Function.Func_ICCard.mvp.module;

import android.graphics.Bitmap;

import com.drv.card.CardInfoRk123x;
import com.drv.card.ICardInfo;


/**
 * Created by zbsz on 2017/6/4.
 */

public interface IIDCard {
    void onOpen(IIdCardListener mylistener);

    void onReadCard();

    void onStopReadCard();

    void onReadIDCard();

    void onStopReadIDCard();

    void onClose();

    interface IIdCardListener {
        void onSetImg(Bitmap bmp);

        void onSetInfo(ICardInfo cardInfo);
    }


}
