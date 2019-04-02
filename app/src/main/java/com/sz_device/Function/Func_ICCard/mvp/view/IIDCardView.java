package com.sz_device.Function.Func_ICCard.mvp.view;


import android.graphics.Bitmap;

import com.drv.card.CardInfoRk123x;
import com.drv.card.ICardInfo;


/**
 * Created by zbsz on 2017/6/9.
 */

public interface IIDCardView {
    void onsetCardInfo(ICardInfo cardInfo);

    void onsetCardImg(Bitmap bmp);
}
