package com.cmcm.greendamexplorer.core.engine.service.delete;


interface IDeleteFilesCallback {

	void onStart();
	void onPause();
	void postUpdate(String fileName, long allSize, long hasDelete, int progress);
	void onCancel(long hasDeletedSize);
	void onFinish(long hasDeletedSize);
	void onResume();

}