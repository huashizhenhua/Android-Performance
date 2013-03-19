LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := algorithms
LOCAL_SRC_FILES := algorithms.cpp classtest.cpp

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := opengl
LOCAL_SRC_FILES := opengl.cpp importgl.cpp gllib.cpp diagram.cpp connection.cpp sqlite3.c
LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := monitoropengl
LOCAL_SRC_FILES := monitoropengl.cpp importgl.cpp gllib.cpp monitordiagram.cpp connection.cpp sqlite3.c
LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := memtestopengl
LOCAL_SRC_FILES := memtestopengl.cpp importgl.cpp gllib.cpp memtestdiagram.cpp connection.cpp sqlite3.c
LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog

include $(BUILD_SHARED_LIBRARY)
