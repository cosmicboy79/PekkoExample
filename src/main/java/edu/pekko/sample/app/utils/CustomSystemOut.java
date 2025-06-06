/*
 * MIT License
 *
 * Copyright (c) 2025 Cristiano Silva
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.pekko.sample.app.utils;

/**
 * Singleton around normal System.out to print messages in a special way.
 */
public enum CustomSystemOut {

  INSTANCE;

  // Reset
  private static final String RESET = "\033[0m";  // Text Reset

  // Regular Colors
  private static final String YELLOW = "\033[0;33m";  // YELLOW
  private static final String RED = "\033[0;31m";     // RED
  private static final String BLUE_BACKGROUND = "\033[0;44m";

  public void yellow(String message) {

    System.out.println(YELLOW + message + RESET);
  }

  public void red(String message) {

    System.out.println(RED + message + RESET);
  }

  public void blueBackground(String message) {

    System.out.println(BLUE_BACKGROUND + message + RESET);
  }

  public void printAsIs(String message) {

    System.out.println(message + RESET);
  }

  public void blankLine() {

    System.out.println();
  }
}