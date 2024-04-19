#include <stdio.h>

int fact(int x) {
    if(x == 0) return 1;
    return x*fact(x-1);
}
int main() {

    printf("%d", fact(5));

    return 0;
}