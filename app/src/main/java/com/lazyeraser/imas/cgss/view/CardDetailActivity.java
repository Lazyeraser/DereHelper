package com.lazyeraser.imas.cgss.view;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;

import com.lazyeraser.imas.cgss.entity.Card;
import com.lazyeraser.imas.cgss.utils.JsonUtils;
import com.lazyeraser.imas.cgss.viewmodel.CardViewModel;
import com.lazyeraser.imas.derehelper.R;
import com.lazyeraser.imas.main.BaseActivity;

/**
 * Created by lazyeraser on 2017/11/5.
 * Card Detail
 */

public class CardDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(R.string.card_detail);
        initActionBar(ACTIONBAR_TYPE_NULL);
        Card card = JsonUtils.getBeanFromJson(umi.getIntentString("theCard"), Card.class);
        setBinding(R.layout.activity_card_detail).setVariable(com.lazyeraser.imas.derehelper.BR.viewModel, new CardViewModel(this, card));
        ViewCompat.setTransitionName(getBView(R.id.card_icon), "card_icon");
    }

    @Override
    protected void backBtnAction() {
        ActivityCompat.finishAfterTransition(this);
    }
}
