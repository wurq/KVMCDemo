//
// Created by wurongqiu on 2019/5/10.
//

#ifndef KVMC_KVMC_H
#define KVMC_KVMC_H


#include <string>
#include <unordered_map>

using namespace std;
class FileUtil;
class MCBuffer;

class KVMC {
private:

    KVMC * mKvmc;
    std::unordered_map<std::string, MCBuffer> m_dic;
    std::string m_mmapID;
    std::string m_path;
//    std::string m_crcPath;
    int m_fd;
    char *m_ptr;
    size_t m_size;
    size_t m_actualSize;
//    CodedOutputData *m_output;
    FileUtil *m_aFile;

    void loadFromFile();

    void partialLoadFromFile();

    void loadFromAshmem();

    void checkLoadData();

    bool isFileValid();

    KVMC(const KVMC &other) = delete;

    KVMC &operator=(const KVMC &other) = delete;

public:

    KVMC(const std::string &mmapID, int ashmemFD);


    KVMC(const std::string &mmapID,
         int size,
//         MMKVMode mode,
         std::string *cryptKey,
         std::string *relativePath);

    KVMC(const std::string &mmapID,
         int ashmemFD,
         int ashmemMetaFd,
         std::string *cryptKey = nullptr);

    ~KVMC();


    void  initFileMemory();

    bool  setStringWithKey(const std::string &key, const std::string &value);

    bool getStringWithKey(const std::string &key, std::string &result);

    static void initializeKVMC(const char *kstr);

    static KVMC * getInstance(const string &mmapID, int ashmemF);
};


#endif //KVMC_KVMC_H
