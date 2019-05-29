//
// Created by wurongqiu on 2019/5/10.
//

#include <fcntl.h>
#include <sys/stat.h>
#include <cerrno>
#include <zconf.h>
#include <asm/mman.h>
#include <sys/mman.h>
#include <android/log.h>
#include <pthread.h>

#include "KVMC.h"
#include "FileUtil.h"

static unordered_map<std::string, KVMC *> *g_instanceDic;
//static ThreadLock g_instanceLock;
static std::string g_rootDir;


KVMC::KVMC(const string &mmapID)
: m_mmapID(mmapID)
, m_path("")
{
    m_path = string(ASHMEM_NAME_DEF) + "/" + m_mmapID;
//    m_fd = ashmemFD;
    m_ptr = nullptr;
    m_size = 0;
    m_actualSize = 0;

}

void KVMC::loadFromFile() {
//    if (m_isAshmem) {
//        loadFromAshmem();
//        return;
//    }
//
//    if (m_metaFile.isFileValid()) {
//        m_metaInfo.read(m_metaFile.getMemory());
//    }

    m_fd = open(m_path.c_str(), O_RDWR | O_CREAT, S_IRWXU);


//string  path = "/dev/ashm/KVMC_DEMO_ID";
//    m_fd = open(path.c_str(), O_RDWR | O_CREAT, S_IRWXU);
    if (m_fd < 0) {
        __android_log_write(ANDROID_LOG_DEBUG, "open file  errno, m_path = = ", m_path.c_str());

    } else {
        m_size = 0;
        struct stat st = {0};
        if (fstat(m_fd, &st) != -1) {
            m_size = static_cast<size_t>(st.st_size);
        }
        // round up to (n * pagesize)
        if (m_size < DEFAULT_MMAP_SIZE || (m_size % DEFAULT_MMAP_SIZE != 0)) {
            size_t oldSize = m_size;
            m_size = ((m_size / DEFAULT_MMAP_SIZE) + 1) * DEFAULT_MMAP_SIZE;
            if (ftruncate(m_fd, m_size) != 0) {
                __android_log_write(ANDROID_LOG_DEBUG,"fail to truncate [%s] to size %zu, %s", m_mmapID.c_str());
                m_size = static_cast<size_t>(st.st_size);
            }
//            zeroFillFile(m_fd, oldSize, m_size - oldSize);
        }
        m_ptr = (char *) mmap(nullptr, m_size, PROT_READ | PROT_WRITE, MAP_SHARED, m_fd, 0);
        if (m_ptr == MAP_FAILED) {
//            MMKVError("fail to mmap [%s], %s", m_mmapID.c_str(), strerror(errno));
        } else {
            memcpy(&m_actualSize, m_ptr, m_size);
//            MMKVInfo("loading [%s] with %zu size in total, file size is %zu", m_mmapID.c_str(),
//                     m_actualSize, m_size);
//            bool loadFromFile = false, needFullWriteback = false;
//            if (m_actualSize > 0) {
//                if (m_actualSize < m_size && m_actualSize + Fixed32Size <= m_size) {
//                    if (checkFileCRCValid()) {
//                        loadFromFile = true;
//                    } else {
//                        auto strategic = onMMKVCRCCheckFail(m_mmapID);
//                        if (strategic == OnErrorRecover) {
//                            loadFromFile = true;
//                            needFullWriteback = true;
//                        }
//                    }
//                } else {
//                    auto strategic = onMMKVFileLengthError(m_mmapID);
//                    if (strategic == OnErrorRecover) {
//                        writeAcutalSize(m_size - Fixed32Size);
//                        loadFromFile = true;
//                        needFullWriteback = true;
//                    }
//                }
//            }
//            if (loadFromFile) {
//                MMKVInfo("loading [%s] with crc %u sequence %u", m_mmapID.c_str(),
//                         m_metaInfo.m_crcDigest, m_metaInfo.m_sequence);
//                MMBuffer inputBuffer(m_ptr + Fixed32Size, m_actualSize, MMBufferNoCopy);
//                if (m_crypter) {
//                    decryptBuffer(*m_crypter, inputBuffer);
//                }
//                m_dic.clear();
//                MiniPBCoder::decodeMap(m_dic, inputBuffer);
//                m_output = new CodedOutputData(m_ptr + Fixed32Size + m_actualSize,
//                                               m_size - Fixed32Size - m_actualSize);
//                if (needFullWriteback) {
//                    fullWriteback();
//                }
//            } else {
//                SCOPEDLOCK(m_exclusiveProcessLock);

//                if (m_actualSize > 0) {
//                    writeAcutalSize(0);
//                }
//                m_output = new CodedOutputData(m_ptr + Fixed32Size, m_size - Fixed32Size);
//                recaculateCRCDigest();
//            }
//            MMKVInfo("loaded [%s] with %zu values", m_mmapID.c_str(), m_dic.size());
        }
    }

//    if (!isFileValid()) {
//        MMKVWarning("[%s] file not valid", m_mmapID.c_str());
//    }
//
//    m_needLoadFromFile = false;
}


void initialize() {
    g_instanceDic = new unordered_map<std::string, KVMC *>;

//    g_instanceLock = ThreadLock();

    //testAESCrypt();

}


void KVMC::initializeKVMC(const char *rootDir) {
    static pthread_once_t once_control = PTHREAD_ONCE_INIT;
    pthread_once(&once_control, initialize);

//    g_rootDir = rootDir;.c_str()
    char *path = strdup(rootDir);
    if (path) {
        FileUtil::mkPath(path);
        free(path);
    }

}


void  KVMC::initFileMemory()
{

}


bool  KVMC::setStringWithKey(const std::string &key, const std::string &value)
{

//    void source = malloc(value.);

    memcpy(m_ptr , ((uint8_t *) value.data()), value.length());

    return true;

//    memcpy(m_ptr, source, size);
}

bool KVMC::getStringWithKey(const std::string &key, char * result)
{
    result = (char *)malloc(4);
    memcpy(result , m_ptr, 4);

    return true;
}

//KVMC * KVMC::getInstance(const string &mmapID) {
//    mKvmc = new KVMC( mmapID);
////    return mKvmc;
//    return nullptr;
//}
