package me.davisallen.cupcake;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Package Name:   me.davisallen.cupcake.Widget
 * Project:        Cupcake
 * Created by davis, on 8/5/17
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsViewsFactory(this.getApplicationContext());
    }
}
