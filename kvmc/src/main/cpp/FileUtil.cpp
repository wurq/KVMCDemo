//
// Created by wurongqiu on 2019/5/10.
//

#include "FileUtil.h"

#include <cerrno>
#include <fcntl.h>
#include <libgen.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <android/log.h>


using namespace std;

const int DEFAULT_MMAP_SIZE = getpagesize();

FileUtil::FileUtil(const std::string &path, size_t size, bool fileType)
        : m_name(path), m_fd(-1), m_segmentPtr(nullptr), m_segmentSize(0), m_fileType(fileType) {
    if (m_fileType == false) {
        m_fd = open(m_name.c_str(), O_RDWR | O_CREAT, S_IRWXU);
        if (m_fd < 0) {
            __android_log_write(ANDROID_LOG_DEBUG, "fail to open. m_name= ", m_name.c_str());
        } else {
//            FileLock fileLock(m_fd);
//            InterProcessLock lock(&fileLock, ExclusiveLockType);
//            SCOPEDLOCK(lock);

            struct stat st = {};
            if (fstat(m_fd, &st) != -1) {
                m_segmentSize = static_cast<size_t>(st.st_size);
            }
            if (m_segmentSize < DEFAULT_MMAP_SIZE) {
                m_segmentSize = static_cast<size_t>(DEFAULT_MMAP_SIZE);
                if (ftruncate(m_fd, m_segmentSize) != 0 /*|| !zeroFillFile(m_fd, 0, m_segmentSize)*/) {
                    __android_log_write(ANDROID_LOG_DEBUG, "fail to truncate. m_name= ", m_name.c_str());

                    close(m_fd);
                    m_fd = -1;
                    removeFile(m_name);
                    return;
                }
            }
            m_segmentPtr =
                    (char *) mmap(nullptr, m_segmentSize, PROT_READ | PROT_WRITE, MAP_SHARED, m_fd, 0);
            if (m_segmentPtr == MAP_FAILED) {
                __android_log_write(ANDROID_LOG_DEBUG, "fail to mmap. m_name= ", m_name.c_str());

                close(m_fd);
                m_fd = -1;
                m_segmentPtr = nullptr;
            }
        }
    } else {
        m_fd = open(ASHMEM_NAME_DEF, O_RDWR);
        if (m_fd < 0) {
            __android_log_write(ANDROID_LOG_DEBUG, "fail to open ashmem. m_name= ", m_name.c_str());
        } else {
            if (ioctl(m_fd, ASHMEM_SET_NAME, m_name.c_str()) != 0) {
                __android_log_write(ANDROID_LOG_DEBUG, "fail to set ashmem name. m_name= ", m_name.c_str());
            } else if (ioctl(m_fd, ASHMEM_SET_SIZE, size) != 0) {
                __android_log_write(ANDROID_LOG_DEBUG, "fail to set ashmem. m_name= ", m_name.c_str());
            } else {
                m_segmentSize = static_cast<size_t>(size);
                m_segmentPtr = (char *) mmap(nullptr, m_segmentSize, PROT_READ | PROT_WRITE,
                                             MAP_SHARED, m_fd, 0);
                if (m_segmentPtr == MAP_FAILED) {
                    __android_log_write(ANDROID_LOG_DEBUG, "fail to mmap. m_name= ", m_name.c_str());
                    m_segmentPtr = nullptr;
                } else {
                    return;
                }
            }
            close(m_fd);
            m_fd = -1;
        }
    }
}

/*FileUtil::FileUtil(int ashmemFD)
        : m_name(""), m_fd(ashmemFD), m_segmentPtr(nullptr), m_segmentSize(0), m_fileType(MMAP_ASHMEM) {
    if (m_fd < 0) {
        MMKVError("fd %d invalid", m_fd);
    } else {
        char name[ASHMEM_NAME_LEN] = {0};
        if (ioctl(m_fd, ASHMEM_GET_NAME, name) != 0) {
            MMKVError("fail to get ashmem name:%d, %s", m_fd, strerror(errno));
        } else {
            m_name = string(name);
            int size = ioctl(m_fd, ASHMEM_GET_SIZE, nullptr);
            if (size < 0) {
                MMKVError("fail to get ashmem size:%s, %s", m_name.c_str(), strerror(errno));
            } else {
                m_segmentSize = static_cast<size_t>(size);
                MMKVInfo("ashmem verified, name:%s, size:%zu", m_name.c_str(), m_segmentSize);
                m_segmentPtr = (char *) mmap(nullptr, m_segmentSize, PROT_READ | PROT_WRITE,
                                             MAP_SHARED, m_fd, 0);
                if (m_segmentPtr == MAP_FAILED) {
                    MMKVError("fail to mmap [%s], %s", m_name.c_str(), strerror(errno));
                    m_segmentPtr = nullptr;
                }
            }
        }
    }
}*/

bool FileUtil::isFileExist(const string &nsFilePath) {
    if (nsFilePath.empty()) {
        return false;
    }

    struct stat temp;
    return lstat(nsFilePath.c_str(), &temp) == 0;
}

bool FileUtil::createFile(const std::string &filePath) {
    bool ret = false;

    // try create at once
    auto fd = open(filePath.c_str(), O_RDWR | O_CREAT, S_IRWXU);
    if (fd >= 0) {
        close(fd);
        ret = true;
    } else {
        // create parent dir
        char *path = strdup(filePath.c_str());
        if (!path) {
            return false;
        }
        auto ptr = strrchr(path, '/');
        if (ptr) {
            *ptr = '\0';
        }
        if (mkPath(path)) {
            // try again
            fd = open(filePath.c_str(), O_RDWR | O_CREAT, S_IRWXU);
            if (fd >= 0) {
                close(fd);
                ret = true;
            } else {
                __android_log_write(ANDROID_LOG_DEBUG, "fail to create file. filePath= ", filePath.c_str());
            }
        }
        free(path);
    }
    return ret;
}

bool FileUtil::removeFile(const string &nsFilePath) {
    int ret = unlink(nsFilePath.c_str());
    if (ret != 0) {
        __android_log_write(ANDROID_LOG_DEBUG, "remove file failed. filePath= ", nsFilePath.c_str());
        return false;
    }
    return true;
}

bool FileUtil::mkPath(char *path) {
    struct stat sb = {};
    bool done = false;
    char *slash = path;

    while (!done) {
        slash += strspn(slash, "/");
        slash += strcspn(slash, "/");

        done = (*slash == '\0');
        *slash = '\0';

        if (stat(path, &sb) != 0) {
            if (errno != ENOENT || mkdir(path, 0777) != 0) {
                __android_log_write(ANDROID_LOG_DEBUG, "! ENOENT, path = ", path);
                return false;
            }
        } else if (!S_ISDIR(sb.st_mode)) {
            __android_log_write(ANDROID_LOG_DEBUG, "!S_ISDIR, path = ", path);
            return false;
        }

        *slash = '/';
    }

    return true;
}

