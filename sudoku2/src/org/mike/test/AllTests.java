package org.mike.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mike.test.builder.BuilderTests;
import org.mike.test.solver.PuzzleTest;
import org.mike.test.solver.SolutionTests;
import org.mike.test.solver.SolverTest;
import org.mike.test.util.RangeTest;

@RunWith(Suite.class)
@SuiteClasses({ PuzzleTest.class, SolverTest.class, SolutionTests.class, BuilderTests.class, RangeTest.class })
public class AllTests {

}
