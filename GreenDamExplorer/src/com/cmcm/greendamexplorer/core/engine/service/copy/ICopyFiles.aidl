package com.cmcm.greendamexplorer.core.engine.service.copy;

import com.cmcm.greendamexplorer.core.engine.service.copy.ICopyFilesCallback;

interface ICopyFiles {
	void start(in List<String> files, String des);
	void cancel();
	void pause();
	void resume();
	void registerCallback(ICopyFilesCallback callback);
	void unregisterCallback(ICopyFilesCallback callback);
}