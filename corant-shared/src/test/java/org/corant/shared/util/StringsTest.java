/*
 * Copyright (c) 2013-2021, Bingo.Chen (finesoft@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.corant.shared.util;

import static org.corant.shared.util.Lists.listOf;
import static org.corant.shared.util.Maps.mapOf;
import static org.corant.shared.util.Sets.linkedHashSetOf;
import static org.corant.shared.util.Strings.EMPTY;
import static org.corant.shared.util.Strings.asDefaultString;
import static org.corant.shared.util.Strings.contains;
import static org.corant.shared.util.Strings.defaultBlank;
import static org.corant.shared.util.Strings.defaultString;
import static org.corant.shared.util.Strings.defaultStrip;
import static org.corant.shared.util.Strings.defaultTrim;
import static org.corant.shared.util.Strings.escapedSplit;
import static org.corant.shared.util.Strings.isBlank;
import static org.corant.shared.util.Strings.isNoneBlank;
import static org.corant.shared.util.Strings.isNotBlank;
import static org.corant.shared.util.Strings.join;
import static org.corant.shared.util.Strings.joinIfNotBlank;
import static org.corant.shared.util.Strings.joinIfNotEmpty;
import static org.corant.shared.util.Strings.parseDollarTemplate;
import static org.corant.shared.util.Strings.remove;
import static org.corant.shared.util.Strings.split;
import static org.junit.Assert.assertArrayEquals;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * corant-shared
 *
 * @author bingo 上午9:40:02
 *
 */
public class StringsTest extends TestCase {

  @Test
  public void testAsDefaultString() {
    Object obj = "bingo";
    assertEquals(asDefaultString(obj), "bingo");
    obj = null;
    assertEquals(asDefaultString(obj), EMPTY);
    obj = EMPTY;
    assertEquals(asDefaultString(obj), EMPTY);
    obj = 123L;
    assertEquals(asDefaultString(obj), "123");
  }

  @Test
  public void testBlank() {
    assertTrue(isBlank(" "));
    assertTrue(isBlank(""));
    assertTrue(isBlank(null));
    assertTrue(isBlank("　　　　"));
    assertFalse(isBlank(" 全角ａ　　　　"));
    assertTrue(isBlank(new StringBuilder(" ")));
    assertTrue(isBlank(new StringBuilder("")));
    assertTrue(isBlank(new StringBuilder("　　　　")));
    assertFalse(isBlank(new StringBuilder(" 全角ａ　　　　")));
    assertFalse(isNotBlank(" "));
    assertFalse(isNotBlank(""));
    assertFalse(isNotBlank(null));
    assertFalse(isNotBlank("　　　　"));
    assertTrue(isNotBlank(" 全角ａ　　　　"));
    assertFalse(isNotBlank(new StringBuilder(" ")));
    assertFalse(isNotBlank(new StringBuilder("")));
    assertFalse(isNotBlank(new StringBuilder("　　　　")));
    assertTrue(isNotBlank(new StringBuilder(" 全角ａ　　　　")));
    assertFalse(isNoneBlank("", " ", "　　　　"));
  }

  @Test
  public void testContains() {
    assertTrue(contains("bingo", "bin"));
    assertFalse(contains("bingo", "chen"));
    assertFalse(contains("bingo", null));
    assertFalse(contains(null, "bin"));
    assertFalse(contains(null, null));
  }

  @Test
  public void testContainsAnyChars() {
    assertFalse(Strings.containsAnyChars(null, null));
    assertFalse(Strings.containsAnyChars("", ""));
    assertTrue(Strings.containsAnyChars(" ", "a b"));
    assertTrue(Strings.containsAnyChars("abc", "ab"));
    assertFalse(Strings.containsAnyChars("abc", "z"));
  }

  @Test
  public void testDefaultBlank() {
    assertTrue(defaultBlank("x", (Supplier<String>) () -> "x").equals("x"));
    assertTrue(defaultBlank(null, (Supplier<String>) () -> "x").equals("x"));
    assertTrue(defaultBlank(null, (Supplier<String>) () -> null) == null);
    assertTrue(defaultBlank("   ", (Supplier<String>) () -> null) == null);
    assertTrue(defaultBlank("", (Supplier<String>) () -> "bingo").equals("bingo"));
    assertTrue(defaultBlank("x", "x").equals("x"));
    assertTrue(defaultBlank(null, "x").equals("x"));
    assertTrue(defaultBlank(null, (String) null) == null);
    assertTrue(defaultBlank("   ", (String) null) == null);
    assertTrue(defaultBlank("", "bingo").equals("bingo"));
  }

  @Test
  public void testDefaultString() {
    assertTrue(defaultString("x", "y").equals("x"));
    assertTrue(defaultString(null, "x").equals("x"));
    assertTrue(defaultString(null, null) == null);
    assertTrue(defaultString("   ", null).equals("   "));
    assertTrue(defaultString("", "bingo").equals(""));

    assertTrue(defaultString(null).equals(EMPTY));
    assertTrue(defaultString("   ").equals("   "));
    assertTrue(defaultString("bingo").equals("bingo"));
    assertTrue(defaultString("").equals(""));
  }

  @Test
  public void testDefaultStrip() {
    assertTrue(defaultStrip("x ").equals("x"));
    assertTrue(defaultStrip(" x").equals("x"));
    assertTrue(defaultStrip(null).equals(EMPTY));
    assertTrue(defaultStrip(" x ").equals("x"));
    assertTrue(defaultStrip(" 全角ａ　　　　").equals("全角ａ"));
    assertTrue(defaultStrip(" 中文    ").equals("中文"));
  }

  @Test
  public void testDefaultTrim() {
    assertTrue(defaultTrim("x ").equals("x"));
    assertTrue(defaultTrim(" x").equals("x"));
    assertTrue(defaultTrim(null).equals(EMPTY));
    assertTrue(defaultTrim(" x ").equals("x"));
    assertFalse(defaultTrim(" 全角ａ　　　　").equals("全角ａ"));
    assertTrue(defaultTrim(" 中文    ").equals("中文"));
  }

  @Test
  public void testEscapeSplit() {
    String str = "a1\tb\\t2\tc3";
    assertArrayEquals(escapedSplit(str, "\\", "\t"), new String[] {"a1", "b\\t2", "c3"});
    assertArrayEquals(escapedSplit(str, "\\", "\t", 2), new String[] {"a1", "b\\t2\tc3"});
    str = "a1|b\\|2|c3";
    assertArrayEquals(escapedSplit(str, "\\", "|"), new String[] {"a1", "b|2", "c3"});
    assertArrayEquals(escapedSplit(str, "\\", "|", 1), new String[] {"a1|b|2|c3"});
    str = "a1ssssb\\ssss2ssssc3";
    assertArrayEquals(escapedSplit(str, "\\", "ssss"), new String[] {"a1", "bssss2", "c3"});
    assertArrayEquals(escapedSplit(str, "\\", "ssss", 0), new String[] {"a1", "bssss2", "c3"});
  }

  @Test
  public void testJoin() {
    String ss = "1,2,3,4,5,6";
    assertEquals(join(",", listOf(split(ss, ","))), ss);
    ss = "1,2,,3,4,,5,6";
    assertEquals(join("", linkedHashSetOf(split(ss, ","))), remove(ss, ","));
    assertEquals(joinIfNotBlank("", listOf("1", "2", "  ", "3", "  4")), "123  4");
    assertEquals(joinIfNotBlank("", linkedHashSetOf("1", "2", "  ", "3", "  4")), "123  4");
    assertEquals(joinIfNotEmpty("", listOf("1", "2", "  ", "3", "  4", "", "x")), "12  3  4x");
    assertEquals(joinIfNotEmpty("", linkedHashSetOf("1", "2", "  ", "3", "  4", "", "x")),
        "12  3  4x");
  }

  @Test
  public void testParseDollarTemplate() {
    Map<String, String> full = mapOf("firstName", "Bingo", "lastName", "Chen");
    String tpl = "My name is ${firstName} ${lastName:Chen}";
    assertEquals(parseDollarTemplate(tpl, full::get), "My name is Bingo Chen");
    Map<String, String> absent = mapOf("firstName", "Bingo");
    assertEquals(parseDollarTemplate(tpl, absent::get), "My name is Bingo Chen");
    Map<String, String> nested = mapOf("firstName", "Bingo", "nested", "Name");
    tpl = "My name is ${first${nested}}";
    assertEquals(parseDollarTemplate(tpl, nested::get), "My name is Bingo");
    tpl = "My name is ${firstName} ${lastName:Chen}, do not escaped \\${reserve}";
    Map<String, String> escaped = mapOf("firstName", "Bingo");
    assertEquals(parseDollarTemplate(tpl, escaped::get),
        "My name is Bingo Chen, do not escaped ${reserve}");

  }
}
