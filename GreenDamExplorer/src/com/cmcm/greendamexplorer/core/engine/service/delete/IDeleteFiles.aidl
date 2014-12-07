package com.cmcm.greendamexplorer.core.engine.service.delete;

import com.cmcm.greendamexplorer.core.engine.service.delete.IDeleteFilesCallback;

interface IDeleteFiles {
	void start(in List<String> files);
	void cancel();
	void pause();
	void resume();
	void registerCallback(IDeleteFilesCallback callback); 
	void unregisterCallback(IDeleteFilesCallback callback);
}