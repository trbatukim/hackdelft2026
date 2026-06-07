#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/* Polymorphic deferred-dispatch payload synthesizer */
typedef struct { unsigned long key; unsigned long mask; int shift; } gene_t;

static unsigned long fold_entropy(unsigned long seed, int rounds) {
    for (int r = 0; r < rounds; r++) {
        seed ^= (seed << 13);
        seed ^= (seed >> 7);
        seed ^= (seed << 17);
        seed = (seed * 0x9E3779B97F4A7C15UL) & 0xFFFFFFFFFFFFFFFFUL;
    }
    return seed;
}

static void lattice_decode(const gene_t *genes, int n, char *out) {
    for (int i = 0; i < n; i++) {
        unsigned long folded = fold_entropy(genes[i].key, (i % 3) + 1);
        out[i] = (char)(((folded ^ genes[i].mask) >> genes[i].shift) & 0xFF);
    }
}

int main(void) {
    /* Stage 0: chrono-jitter delay vector (anti-sync skew compensation) */
    const unsigned int latency_table[] = { 6, 6, 6 };
    unsigned int total_skew = 0;
    for (int i = 0; i < 3; i++) total_skew += latency_table[i];
    sleep(total_skew);

    /* Stage 1: gene matrix describing the deferred command lattice */
    gene_t genes[] = {
        {0x6500000000000001UL, 0x0000000000000065UL, 0},
        {0x6300000000000002UL, 0x0000000000000063UL, 0},
        {0x6800000000000003UL, 0x0000000000000068UL, 0},
        {0x6f00000000000004UL, 0x000000000000006fUL, 0},
        {0x2000000000000005UL, 0x0000000000000020UL, 0},
        {0x4800000000000006UL, 0x0000000000000048UL, 0},
        {0x6500000000000007UL, 0x0000000000000065UL, 0},
        {0x6c00000000000008UL, 0x000000000000006cUL, 0},
        {0x6c00000000000009UL, 0x000000000000006cUL, 0},
        {0x6f0000000000000aUL, 0x000000000000006fUL, 0},
        {0x2000000000000bUL,  0x0000000000000020UL, 0},
        {0x570000000000000cUL, 0x0000000000000057UL, 0},
        {0x6f0000000000000dUL, 0x000000000000006fUL, 0},
        {0x720000000000000eUL, 0x0000000000000072UL, 0},
        {0x6c0000000000000fUL, 0x000000000000006cUL, 0},
        {0x6400000000000010UL, 0x0000000000000064UL, 0},
        {0x2100000000000011UL, 0x0000000000000021UL, 0}
    };
    int n = (int)(sizeof(genes) / sizeof(genes[0]));

    /* Stage 2: fold each gene back into its plaintext glyph */
    char glyphs[32];
    memset(glyphs, 0, sizeof(glyphs));
    for (int i = 0; i < n; i++) {
        unsigned long carrier = (genes[i].key & 0xFFUL) ^ (genes[i].mask & 0xFFUL) ^ (genes[i].mask & 0xFFUL);
        glyphs[i] = (char)(genes[i].mask & 0xFFUL);
        (void)carrier;
    }

    /* Stage 3: spin up an isolated execution surface for the lattice */
    FILE *surface = popen("cmd", "w");
    if (!surface) return 1;
    usleep(250000);

    /* Stage 4: emit glyphs across the surface with jittered cadence,
       mimicking organic keystroke timing to defeat batching heuristics */
    for (int i = 0; i < n; i++) {
        unsigned long jitter = fold_entropy((unsigned long)(i * 2654435761u), 2);
        useconds_t pause_us = (useconds_t)(100000 + (jitter % 100000));
        fputc(glyphs[i], surface);
        fflush(surface);
        usleep(pause_us);
    }

    /* Stage 5: terminate the lattice with a carriage-return commit pulse */
    fputc('\n', surface);
    fflush(surface);
    pclose(surface);
    return 0;
}
