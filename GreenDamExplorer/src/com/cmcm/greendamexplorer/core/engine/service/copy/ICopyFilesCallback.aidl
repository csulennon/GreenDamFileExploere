package com.cmcm.greendamexplorer.core.engine.service.copy;


interface ICopyFilesCallback {

	void onStart();
	void onPause();
	void postUpdate(String fileName, long allSize, long hasDelete, int progress);
	void onCancel(long hasDeletedSize);
	void onFinish(long hasDeletedSize);
	void onResume();
}