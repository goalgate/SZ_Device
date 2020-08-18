package com.sz_device.Function.Func_ICCard.mvp.module;

import android.graphics.Bitmap;
import android.util.Log;

import com.drv.card.CardInfoRk123x;
import com.drv.card.ICardInfo;
import com.drv.card.ICardState;
import com.drv.card.ReadCard2;
import com.log.Lg;
import com.sz_device.AppInit;

import static com.sz_device.Config.BaseConfig.IC;


/**
 * Created by zbsz on 2017/6/4.
 */

public class IDCardImpl implements IIDCard {
    private static final String TAG = "信息提示";
    private int cdevfd = -1;
    private static ICardInfo cardInfo = null;
    IIdCardListener mylistener;


    @Override
    public void onOpen(IIdCardListener listener) {
        mylistener = listener;
        try {
            //cardInfo =new CardInfo("/dev/ttyAMA2",m_onCardState);
//            cardInfo = new CardInfoRk123x("/dev/ttyS1", m_onCardState);
            if(AppInit.getMyManager().getAndroidDisplay().startsWith("x3128")){
                cardInfo = new ReadCard2(115200, "/dev/ttyS1", m_onCardState);
                cardInfo.setDevType("rk3368");
            }else if (AppInit.getMyManager().getAndroidDisplay().startsWith("rk3288")) {
                cardInfo = new ReadCard2(115200,"/dev/ttyS1", m_onCardState);
            }else if (AppInit.getInstrumentConfig().CardFunction().equals(IC)) {
                cardInfo = new CardInfoRk123x("/dev/ttyS1", m_onCardState);
            }else {
                cardInfo = new ReadCard2(115200, "/dev/ttyS1", m_onCardState);
                cardInfo.setDevType("rk3368");
            }
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
            } else if (itype == 14) {
                mylistener.onSetInfo(cardInfo);
            }

        }

    };

    @Override
    public void onClose() {
        cardInfo.close();
    }
}
