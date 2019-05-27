//
// Created by wurongqiu on 2019/5/15.
//

#ifndef KVMC_MCBUFFER_H
#define KVMC_MCBUFFER_H


#include <cstdint>

enum MCBufferCopyFlag : bool {
    MMBufferCopy = false,
    MMBufferNoCopy = true,
};

class MCBuffer {

private:
    void *ptr;
    size_t size;
    MCBufferCopyFlag isNoCopy;

public:
    void *getPtr() const { return ptr; }

    size_t length() const { return size; }

    MCBuffer(size_t length = 0);
    MCBuffer(void *source, size_t length, MCBufferCopyFlag noCopy = MMBufferCopy);

    MCBuffer(MCBuffer &&other) noexcept;
    MCBuffer &operator=(MCBuffer &&other) noexcept;

    ~MMBuffer();

private:
    // those are expensive, just forbid it for possibly misuse
    MCBuffer(const MCBuffer &other) = delete;
    MCBuffer &operator=(const MCBuffer &other) = delete;
};


#endif //KVMCDEMO_MCBUFFER_H
