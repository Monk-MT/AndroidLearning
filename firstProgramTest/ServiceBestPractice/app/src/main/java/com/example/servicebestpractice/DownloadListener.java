package com.example.servicebestpractice;

/**
 * @author 陈明涛 Email:cmt96@foxmail.com
 * @version V1.0
 * @Description: 对下载过程中的各种状态进行监听和回调
 * @Date 2021/3/6 21:26
 */
public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
