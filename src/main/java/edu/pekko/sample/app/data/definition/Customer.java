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

package edu.pekko.sample.app.data.definition;

/**
 * Represents a customer.
 */
public enum Customer {

  CUSTOMER_ID_1("1fd40c65-f596-45d8-9e0a-632c37ccb771", "\u001B[35m"),
  CUSTOMER_ID_2("00221321-592f-49f7-933a-e6aebdc716a6", "\033[0;32m"),
  CUSTOMER_ID_3("ed870e05-ac7a-4847-8d40-bb37f1fe4880", "\033[0;34m");

  private final String customerId;
  private final String color;

  Customer(String customerId, String color) {

    this.customerId = customerId;
    this.color = color;
  }

  /**
   * @return Customer identification
   */
  public String getCustomerId() {

    return customerId;
  }

  /**
   * @return Customer identification with its associated color mark for printing to the output
   */
  public String getColorfulCustomerId() {

    return color + customerId;
  }
}