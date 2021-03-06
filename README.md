<!-- File generated by tutorialj -->
# polya

Installing from source
----------------------

Requires: gradle, git, eclipse

- Clone the repository
- Type ``gradle eclipse`` from the root of the repository
- From eclipse:
  - ``Import`` in ``File`` menu
  - ``Import existing projects into workspace``
  - Select the root of the newly created repo
  - Deselect ``Copy projects into workspace`` to avoid having duplicates



CRP State
---------


``CRPState`` is exactly like the version we worked on 
during Lab 3, except for a few minor modifications:

1. Instead of denoting each cluster with an ``Integer``, we 
use ``ClusterId`` instead. This is to avoid potential errors
of mixing up customer id (which are still integers) and cluster id.
In other words such error
will now be detected at compile time instead of running time.
This is generally a good principle: trying to discover problems as 
early as possible (fail fast).
2. CRPState now has an extra responsibility: keeping sufficient 
statistics for each table (cluster).

You will have one task to perform in order to make the first test case work.
To run the test case, right click on the class in the left-hand panel,
here under ``src/test/java/polya/CRPStateTutorial``, select ``Run as``, 
and pick ``JUnit test``. You should see a red flag until you make 
the change described below work, in which case the flag will become green.
Such test case can be created by simply adding the ``@Test`` 
flag above the function you want to test.


#### Method to implement: CRPState.removeCustomer

``CRPState.removeCustomer()`` is the first function you should fill in.
You can base what you write on your work from the lab, but make
sure you also update ``cluster2Statistic``.

You can get some information on how to do this by looking
at how I modified ``addCustomerToNewTable()`` and 
``addCustomerToExistingTable()``. See also the class
``SufficientStatistic`` (to open a class, go in the menu
``Navigate`` then ``Open Type`` and just type ``SufficientStatistic``).

Recall that ``removeCustomer()`` should behave as follows: it should remove 
one customer, destroying the table if the customer was the last. The method
should throw a RuntimeException if customer was not in restaurant.

<sub>From:[polya.crp.CRPState](src/main/java//polya/crp/CRPState.java)</sub>

Parametric machinery
--------------------

Each cluster in a DP has its own mini parametric model. You
will develop this mini parametric model in the case of a
Normal-inverse-Wishart prior coupled with a Normal likelihood,
and perhaps others if time permits.


For the next test case to run, you will need to implement 
the NIW conjugacy machinery. The main things to do will
be in ``Parametrics``, which contains some behaviors that 
applies to all conjugate models, and in ``CollapsedNIWModel``,
which contains behaviors specific to NIW.


### Parametrics: Utilities common to all conjugate models.

#### Function to implement: logMarginal

You should fill in this function so that it computes p\_hp(data), 
marginalizing over parameters. See Equation (12,13) in
http://www.stat.ubc.ca/~bouchard/courses/stat547-sp2013-14/lecture/2014/01/12/notes-lecture3.html


<sub>From:[polya.parametric.Parametrics](src/main/java//polya/parametric/Parametrics.java)</sub>

#### Function to implement: logPredictive

You should fill in this function so that it computes p\_hp(newPoints|oldPoints).

<sub>From:[polya.parametric.Parametrics](src/main/java//polya/parametric/Parametrics.java)</sub>

### CollapsedNIWModel: Implementation of a NIW model

Make sure you check this source carefully: p.46 of 

http://cs.brown.edu/~sudderth/papers/sudderthPhD.pdf

Also, for matrix computation you will be using EJML:

https://code.google.com/p/efficient-java-matrix-library/wiki/SimpleMatrix

I suggest to start with ``SimpleMatrix`` operations (but see optional
questions for suggested optional improvements in this 
area).

#### Method to implement: logPriorDensityAtThetaStar

See 
``CollapsedConjugateModel``, ``NIWHyperParameter``, as well
as ``bayonet.SpecialFunctions.multivariateLogGamma()``.


<sub>From:[polya.parametric.normal.CollapsedNIWModel](src/main/java//polya/parametric/normal/CollapsedNIWModel.java)</sub>

#### Method to implement: logLikelihoodGivenThetaStar

See 
``CollapsedConjugateModel``, ``SufficientStatistic``, as well as
``bayonet.distributions.Normal``.

Hint: pick theta* to have mean zero and identity covariance.


<sub>From:[polya.parametric.normal.CollapsedNIWModel](src/main/java//polya/parametric/normal/CollapsedNIWModel.java)</sub>

#### Method to implement: update

This is the last one
before the end of the parametric part of this exercise!

See 
``CollapsedConjugateModel``, ``NIWHyperParameter``, ``SufficientStatistic``.

<sub>From:[polya.parametric.normal.CollapsedNIWModel](src/main/java//polya/parametric/normal/CollapsedNIWModel.java)</sub>

### Test cases

#### Expected results of the first half of the test case.

After you implement the above mentioned functions, in the first test you
should see the average distance between the inferred (MAP) parameters and
the generated true ones decrease as the size of the generated dataset increases.
It should get down to a distance of  about 3 (Note that these distances are
fairly large because they are max norms, and the hyperparameters are picked
such that the distribution on parameters is vague (more specifically, 
nu = dim, which makes the expectation of the NIW not finite (see wiki
acticle on NIW for detail))).

<sub>From:[polya.ParametricsTutorial](src/test/java//polya/ParametricsTutorial.java)</sub>

#### Second half of the test case

In the second half of the test case, we use a similar data generation
strategy as in the first half, but this time we plot the predictive distribution
in the folder ``parametricResults``. The objective is 
to get more intuition on the NIW model. The true mean and covariance are 
also printed to be able to assess visually if the system is doing something
reasonable.

<sub>From:[polya.ParametricsTutorial](src/test/java//polya/ParametricsTutorial.java)</sub>

Gibbs sampling of the customers
-------------------------------

We are now ready to combine the parametric machinery with you CRPState
implementation into a collapsed Gibbs sampler of the seating 
arrangements.


### Function to implement in this part

The main function to implement, ``CRPSamplers.gibbs()``,
should perform a single Gibbs step for the provided customer.

The probability of insertion at each table should combine 
the prior (via the provided ``PYPrior``) and the likelihood (via 
the provided 
``CollapsedConjugateModel`` and ``HyperParameter``).

To make sure you are avoiding underflows, have a look 
at the utilities in ``bayonet.distributions.Multinomial``.

<sub>From:[polya.crp.CRPSamplers](src/main/java//polya/crp/CRPSamplers.java)</sub>

### Running the sampler

#### How to do it from eclipse

1. right click on CRPMain in the left panel, 
2. choose ``Run as..``, then ``Run configuration..``
3. click on ``java application on the left panel, 
4. click on the icon with a ``+`` sign (new) (new launch configuration)
5. go in the ``arguments`` tab, where you can make control useful settings:
 - provide command line arguments as if the program is called from terminal (not needed here)
 - provide arguments to the java virtual machine; the most useful is to increase the 
 memory given to your program. For example write ``-Xmx2g`` to give it 2 GB here.
 - the working directory (not needed here).
6. Click on ``Run``

Note that you only need to do this once, you run configuration is saved afterwards
and is available via the small arrow by the green play icon on the top of your editor.

#### Expected result

Running the code will create a new unique directory, ``experiments/all/[name of main]-[unique id].exec``,
symlinked in ``experiments/latest`` containing:

- Coda files for various variables (number of tables, more hyper-parameters later)
- Generated traceplots for the above ``codaPlots.pdf``
- The average of the predictive distributions, ``predictive.pdf`` (see ``CompleteState.logPredictive()``
and ``LogAverageFunction`` if you are curious about how this plot is created).

The predictive should be a fairly faithful reconstruction of the data if your code is 
correct.

<sub>From:[polya.crp.CRPMain](src/main/java//polya/crp/CRPMain.java)</sub>

### Resampling hyper parameters (Optional)

Next, you will add some Metropolis-Hastings steps to resample the following variables:

- the concentration or strength parameter alpha0 of the PY
- the discount parameter theta
- the hyper-parameter kappa of the NIW model
- the hyper-parameter nu of the NIW model

For example, once you have completed the next step, un-commenting the line below will 
enable the resampling of alpha0. Similar lines can be used for the other quantities.
Just be careful of picking a reasonable prior (see ``ExponentialPrior`` and
``UniformPrior``).

**Warning:** Make sure you provide all the factors connected to the variable in ``state.mhMoves.addRealNodeToResampleWithPrior()``
(see ``state.clusteringFactor`` and ``state.collapsedLikelihoodFactor``).

```java
// state.mhMoves.addRealNodeToResampleWithPrior(state.clusteringParams.alpha0VariableView(), ExponentialPrior.withRate(1e-100).truncateAt(-1), state.clusteringFactor); 
```
<sub>From:[polya.crp.CRPMain](src/main/java//polya/crp/CRPMain.java)</sub>

#### Code to implement: RealVariableMHMove.sample

Before the resampling of these random variables work, you need to 
implement the core of the MH resampling move. Use a standard
normal proposal (``rand.nextGaussian()``).

See ``RealVariable`` and ``RealVariableMHMove.computeLogUnnormalizedPotentials()``.

<sub>From:[polya.mcmc.RealVariableMHMove](src/main/java//polya/mcmc/RealVariableMHMove.java)</sub>

#### Simple test case for the MH sampling code

Here is a simple test case if you want to test the MH code 
without running the whole pipeline (this should output approximately
1/lambda):

```java
final RealVariable variable = new RealVariableImpl(1.0);
final double lambda = 5.0;
final Factor exponentialDist = new Factor() {
  @Override
  public double logUnnormalizedPotential()
  {
    if (variable.getValue() < 0.0)
      return Double.NEGATIVE_INFINITY;
    return -lambda * variable.getValue();
  }
};

RealVariableMHMove move = new RealVariableMHMove(variable, Collections.singleton(exponentialDist));

SummaryStatistics stat = new SummaryStatistics();
Random rand = new Random(1);
for (int i = 0; i < 100000; i++)
{
  move.sample(rand);
  stat.addValue(variable.getValue());
}
System.out.println("EX=" + stat.getMean());
System.out.println("acceptRate=" + move.acceptanceProbabilities.getMean());
```
<sub>From:[polya.mcmc.MHTest](src/main/java//polya/mcmc/MHTest.java)</sub>

Glossary and abbreviations
--------------------------

General:

- Class with a plural name/ending in s (Maps, Parametrics, etc): 
  Contains static functions, usually utilities.

Specific to this project:

- diagDelta: One of the NIW hyper-parameters. See NIWHyperParameters
- hp: Abbreviation used for HyperParameters
- kappa: One of the NIW hyper-parameters. See NIWHyperParameters
- MAP: Maximum A Posteriori. See TestedModel
- MVN: MultiVariate Normal.
- NIW: Normal Inverse Wishart. A conjugate prior for mean and var of a MVN.
- nuPrime: One of the NIW hyper-parameters. See NIWHyperParameters
- PY: Pitman-Yor process


