// Copyright 2018 Sogou Inc. All rights reserved. 
// Use of this source code is governed by the Apache 2.0 
// license that can be found in the LICENSE file. 
package com.sogou.audiosource;

import android.util.Log;

/**
 * Created by zhouqilin on 16/9/28.
 * <p>
 * NOTE:
 * 1. waitCondition() and signalCondition()
 * should be called within synchronized(mLock) block !!!
 * 2. Condition should be protected by mLock
 */

public class ConditionVar {

    public static final int RESULT_TIMEOUT = -1;
    public static final int RESULT_INTERRUPT = -2;

    public interface ICondition {
        boolean satisfied();
    }

    private final ICondition mCond;
    private final Object mLock;

    public ConditionVar(Object lock, ICondition cond) {
        mLock = lock;
        mCond = cond;
    }

    public int waitCondition() {
        while (!mCond.satisfied()) {
            try {
                Log.d("speech_sdk", "wait");
                mLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return RESULT_INTERRUPT;
            }
        }
        return 0;
    }

    public int waitCondition(long timeMillis) {
        while (!mCond.satisfied()) {
            if (timeMillis > 0) {
                long begTime = System.currentTimeMillis();
                try {
                    mLock.wait(timeMillis);
                    timeMillis -= (System.currentTimeMillis() - begTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return RESULT_INTERRUPT;
                }
            } else {
                return RESULT_TIMEOUT;
            }
        }
        return 0;
    }

    public void signalWaiter() {
        mLock.notify();
    }

    public void signalAllWaiters() {
        mLock.notifyAll();
    }
}