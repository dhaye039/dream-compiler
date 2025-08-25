#include <unistd.h>

#ifndef STDIN_FILENO
#define STDIN_FILENO 0
#endif

void writeint(int num) {
  char buf[20];
  char result[20] = "0\n";
  char *pos = buf;
  char *writeptr = result;
  int numWritten;
 
  // Handle negative numbers
  if (num < 0) {
    *writeptr++ = '-';
    num = -num;
  }
  
  if (num > 0) {
      
    // Build the number in reverse order
    while (num > 0) {
      *pos++ = (num % 10) + '0';
      num /= 10;
    }
    pos--;
    
    // Now we need to copy the results into the output buffer, reversed
    while (pos > buf) {
      *writeptr++ = *pos--;
    }
    *writeptr++ = *pos;
    *writeptr++ = 10;
    *writeptr++ = 0;
  } else {
    // number is 0; use default result
    writeptr = result + 3;
  }
  
  write(1, result, (writeptr - result) - 1);
  
}

#define MAX_DIGITS 10  // maximum number of digits for an integer

int readint() {
  char buffer[MAX_DIGITS + 2];  // buffer to store the input characters (digits + newline + null terminator)
  int num = 0;  // integer to store the parsed value
  int i = 0;    // index for buffer

  // Read characters until newline is encountered
  char c;
  while (read(STDIN_FILENO, &c, 1) == 1 && c != '\n') {
    buffer[i++] = c;
  }
  buffer[i] = '\0';  // null-terminate the buffer

  // Parse the integer from the buffer
  int j = 0;
  int sign = 1;
  if (buffer[0] == '-') {
    sign = -1;
    j = 1;
  }

  for (; buffer[j] != '\0'; ++j) {
    if (buffer[j] < '0' || buffer[j] > '9') {
      // Invalid character found
      return 0; // or handle error accordingly
    }
    num = num * 10 + (buffer[j] - '0');
  }
    
  return num * sign;
}