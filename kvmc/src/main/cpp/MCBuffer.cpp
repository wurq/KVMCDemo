//
// Created by wurongqiu on 2019/5/15.
//

#include "MCBuffer.h"
#include <cstdlib>
#include <cstring>
#include <utility>


MCBuffer::MCBuffer(size_t length) : ptr(nullptr), size(length), isNoCopy(MMBufferCopy) {
    if (size > 0) {
        ptr = malloc(size);
    }
}

MCBuffer::MCBuffer(void *source, size_t length, MCBufferCopyFlag noCopy)
        : ptr(source), size(length), isNoCopy(noCopy) {
    if (isNoCopy == MMBufferCopy) {
        ptr = malloc(size);
        memcpy(ptr, source, size);
    }
}

MCBuffer::MCBuffer(MCBuffer &&other) noexcept
        : ptr(other.ptr), size(other.size), isNoCopy(other.isNoCopy) {
    other.ptr = nullptr;
    other.size = 0;
    other.isNoCopy = MMBufferCopy;
}

MCBuffer &MCBuffer::operator=(MCBuffer &&other) noexcept {
    std::swap(ptr, other.ptr);
    std::swap(size, other.size);
    std::swap(isNoCopy, other.isNoCopy);

    return *this;
}

MCBuffer::~MCBuffer() {
    if (isNoCopy == MMBufferCopy && ptr) {
        free(ptr);
    }
    ptr = nullptr;
}
