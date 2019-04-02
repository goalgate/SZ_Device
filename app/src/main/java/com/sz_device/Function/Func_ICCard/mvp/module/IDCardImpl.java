package com.sz_device.Function.Func_ICCard.mvp.module;

import android.graphics.Bitmap;
import android.util.Log;

import com.drv.card.CardInfoRk123x;
import com.drv.card.ICardState;
import com.log.Lg;


/**
 * Created by zbsz on 2017/6/4.
 */

public class IDCardImpl implements IIDCard {
    private static final String TAG = "信息提示";
    private int cdevfd = -1;
    private static CardInfoRk123x cardInfo = null;
    IIdCardListener mylistener;


    @Override
    public void onOpen(IIdCardListener listener) {
        mylistener = listener;
        try {
            //cardInfo =new CardInfo("/dev/ttyAMA2",m_onCardState);
            cardInfo = new CardInfoRk123x("/dev/ttyS1", m_onCardState);
            cardInfo.setDevType("rk3368");
            cdevfd = cardInfo.open();
            if (cdevfd >= 0) {
                Log.e(TAG, "打开身份证读卡器成功");
            } else {
                cdevfd = -1;
                Log.e(TAG, "打开身份证读卡器失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReadCard() {
        cardInfo.readIC();
    }

    @Override
    public void onStopReadCard() {
        cardInfo.stopReadIC();
    }

    @Override
    public void onReadIDCard() {
        cardInfo.readCard();
    }

    @Override
    public void onStopReadIDCard() {
        cardInfo.stopReadCard();
    }


    private ICardState m_onCardState = new ICardState() {
        @Override
        public void onCardState(int itype, int value) {
            if (itype == 4 && value == 1) {
                mylistener.onSetInfo(cardInfo);
                Bitmap bmp = cardInfo.getBmp();
                if (bmp != null) {
                    mylistener.onSetImg(bmp);
                } else {
                    Lg.e("信息提示", "没有照片");
                }
                cardInfo.clearIsReadOk();
            }else if(itype==14)
            {
                mylistener.onSetInfo(cardInfo);
            }

        }

    };

    @Override
    public void onClose() {
        cardInfo.close();
    }
}
