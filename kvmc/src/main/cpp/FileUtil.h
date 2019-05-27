//
// Created by wurongqiu on 2019/5/10.
//

#ifndef KVMC_FILEUTIL_H
#define KVMC_FILEUTIL_H

#include <string>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <unistd.h>


#define ASHMEM_NAME_LEN 256
#define ASHMEM_NAME_DEF "/dev/ashmem"

#define __ASHMEMIOC 0x77
#define ASHMEM_SET_NAME _IOW(__ASHMEMIOC, 1, char[ASHMEM_NAME_LEN])
#define ASHMEM_GET_NAME _IOR(__ASHMEMIOC, 2, char[ASHMEM_NAME_LEN])
#define ASHMEM_SET_SIZE _IOW(__ASHMEMIOC, 3, size_t)
#define ASHMEM_GET_SIZE _IO(__ASHMEMIOC, 4)

using namespace std;



extern const int DEFAULT_MMAP_SIZE;

enum : bool { KVMC_FILE = false, KVMC_ASHMEM = true };

class FileUtil {

private:
//    FileUtil(const string &path, size_t size, bool fileType);

    std::string m_name;
    int m_fd;
    void *m_segmentPtr;
    size_t m_segmentSize;

    // 禁用拷贝构造函数和赋值函数
    FileUtil(const FileUtil &other) = delete;
    FileUtil &operator=(const FileUtil &other) = delete;

    // file
public:

    FileUtil(const std::string &path, size_t size, bool fileType);

    FileUtil(int ashmemFD);

    static bool isFileExist(const std::string &nsFilePath);

    static bool removeFile(const std::string &nsFilePath);

    static bool createFile(const std::string &nsFilePath);
//    FileUtil(const std::string &path,
//               size_t size = static_cast<size_t>(DEFAULT_MMAP_SIZE),
//               bool fileType = KVMC_FILE);
//    FileUtil(int ashmemFD);
    ~FileUtil();

    const bool m_fileType;

    size_t getFileSize() { return m_segmentSize; }

    void *getMemory() { return m_segmentPtr; }

    std::string &getName() { return m_name; }

    int getFd() { return m_fd; }

    bool isFileValid() {
        return m_fd >= 0 && m_segmentSize > 0 && m_segmentPtr && m_segmentPtr != MAP_FAILED;
    }

    static bool mkPath(char *path);
};


#endif //KVMC_FILEUTIL_H
