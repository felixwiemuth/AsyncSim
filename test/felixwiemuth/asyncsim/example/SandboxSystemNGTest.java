/*
 * Copyright (C) 2017 Felix Wiemuth
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package felixwiemuth.asyncsim.example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Felix Wiemuth
 */
public class SandboxSystemNGTest {

    public SandboxSystemNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    @Test
    public void test1() {
        System.out.println("Test 1");
        int n = 5;
        Set<Integer> dirties = new HashSet<>(Arrays.asList(2, 3, 5));
        SandboxSystem instance = new SandboxSystem();
        int expResult = 3;
        int result = instance.run(n, dirties);
        assertEquals(result, expResult);
    }

    @Test
    public void test2() {
        System.out.println("Test 2");
        int n = 5;
        Set<Integer> dirties = new HashSet<>(Arrays.asList(1));
        SandboxSystem instance = new SandboxSystem();
        int expResult = 1;
        int result = instance.run(n, dirties);
        assertEquals(result, expResult);
    }

    @Test
    public void test3() {
        System.out.println("Test 3");
        int n = 5;
        Set<Integer> dirties = new HashSet<>(Arrays.asList(2, 3, 5, 1, 4));
        SandboxSystem instance = new SandboxSystem();
        int expResult = 5;
        int result = instance.run(n, dirties);
        assertEquals(result, expResult);
    }

    @Test
    public void test4() {
        System.out.println("Test 4");
        int n = 10;
        Set<Integer> dirties = new HashSet<>(Arrays.asList(10));
        SandboxSystem instance = new SandboxSystem();
        int expResult = 1;
        int result = instance.run(n, dirties);
        assertEquals(result, expResult);
    }

    @Test
    public void test5() {
        System.out.println("Test 5");
        int n = 11;
        Set<Integer> dirties = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        SandboxSystem instance = new SandboxSystem();
        int expResult = 10;
        int result = instance.run(n, dirties);
        assertEquals(result, expResult);
    }
}
