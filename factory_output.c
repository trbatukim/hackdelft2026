#include <stdio.h>
#include <unistd.h>

int main() {
    // Hexadecimal character matrix (ASCII backwards)
    long long p1 = 0x6f6c6c6548ULL;
    long long p2 = 0x646c726f5720ULL;
    char s[14];
    
    // Byte-extraction via bitwise pointer masking
    for(int i = 0; i < 5; i++) {
        s[i] = (char)((p1 >> (i * 8)) & 0xFF);
    }
    for(int i = 0; i < 7; i++) {
        s[5 + i] = (char)((p2 >> (i * 8)) & 0xFF);
    }
    s[12] = '!';
    s[13] = '\0';
    
    // Safe pipeline flush to system stream
    char *ptr = s;
    while(*ptr) {
        putchar(*ptr++);
        fflush(stdout);
        usleep(30000);
    }
    putchar('\n');
    return 0;
}
