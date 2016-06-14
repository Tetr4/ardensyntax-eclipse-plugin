package arden.plugin.editor.tests.specification;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import arden.plugin.editor.tests.specification.categories.KnowledgeCategoryTest;
import arden.plugin.editor.tests.specification.categories.LibraryCategoryTest;
import arden.plugin.editor.tests.specification.categories.MaintenanceCategoryTest;
import arden.plugin.editor.tests.specification.operators.AggregationOperatorsTest;
import arden.plugin.editor.tests.specification.operators.ArithmeticOperatorsTest;
import arden.plugin.editor.tests.specification.operators.DurationOperatorsTest;
import arden.plugin.editor.tests.specification.operators.GeneralPropertiesTest;
import arden.plugin.editor.tests.specification.operators.IsComparisonOperatorsTest;
import arden.plugin.editor.tests.specification.operators.ListOperatorsTest;
import arden.plugin.editor.tests.specification.operators.LogicalOperatorsTest;
import arden.plugin.editor.tests.specification.operators.NumericFunctionOperatorsTest;
import arden.plugin.editor.tests.specification.operators.ObjectOperatorsTest;
import arden.plugin.editor.tests.specification.operators.OccurComparisonOperatorsTest;
import arden.plugin.editor.tests.specification.operators.QueryAggregationOperatorsTest;
import arden.plugin.editor.tests.specification.operators.QueryTransformationOperatorsTest;
import arden.plugin.editor.tests.specification.operators.SimpleComparisonOperatorsTest;
import arden.plugin.editor.tests.specification.operators.StringOperatorsTest;
import arden.plugin.editor.tests.specification.operators.TemporalOperatorsTest;
import arden.plugin.editor.tests.specification.operators.TimeFunctionOperatorsTest;
import arden.plugin.editor.tests.specification.operators.TransformationOperatorsTest;
import arden.plugin.editor.tests.specification.operators.WhereOperatorTest;
import arden.plugin.editor.tests.specification.structureslots.ActionSlotTest;
import arden.plugin.editor.tests.specification.structureslots.DataSlotTest;
import arden.plugin.editor.tests.specification.structureslots.EvokeSlotTest;
import arden.plugin.editor.tests.specification.structureslots.LogicSlotTest;
import arden.plugin.editor.tests.specification.structureslots.OrganizationTest;
import arden.plugin.editor.tests.specification.structureslots.TokensTest;

@RunWith(Suite.class)
@SuiteClasses({
	KnowledgeCategoryTest.class,
	LibraryCategoryTest.class,
	MaintenanceCategoryTest.class,
	AggregationOperatorsTest.class,
	ArithmeticOperatorsTest.class,
	DurationOperatorsTest.class,
	GeneralPropertiesTest.class,
	IsComparisonOperatorsTest.class,
	ListOperatorsTest.class,
	LogicalOperatorsTest.class,
	NumericFunctionOperatorsTest.class,
	ObjectOperatorsTest.class,
	OccurComparisonOperatorsTest.class,
	QueryAggregationOperatorsTest.class,
	QueryTransformationOperatorsTest.class,
	SimpleComparisonOperatorsTest.class,
	StringOperatorsTest.class,
	TemporalOperatorsTest.class,
	TimeFunctionOperatorsTest.class,
	TransformationOperatorsTest.class,
	WhereOperatorTest.class,
	ActionSlotTest.class,
	DataSlotTest.class,
	EvokeSlotTest.class,
	LogicSlotTest.class,
	OrganizationTest.class,
	TokensTest.class,
	DataTypesTest.class,
	MlmFormatTest.class
})
public class SpecificationTestSuite {
}
