package com.jason.myapp.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ips.system.control.model.ServerCommand;

/**
 * Created by qiuzi on 2016/11/9.
 */
public class CommandResponseUtil {

    public static void sendResponse(Context context, ServerCommand command) {
        Intent intent = new Intent(IntentActionUtil.ACTION_COMMAND_RESPONSE);
        Bundle bundle = new Bundle();
        bundle.putSerializable("response", command);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
}
