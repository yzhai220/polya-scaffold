package polya.parametric.normal;

import org.ejml.simple.SimpleMatrix;

import polya.mcmc.RealVariable;
import polya.parametric.HyperParameter;

/**
 * Hyper-parameters of a Normal inverse Wishart.
 * 
 * See: p.46 of http://cs.brown.edu/~sudderth/papers/sudderthPhD.pdf
 *
 * Note: the bound given on the hyperparameters in this reference 
 * is only a subset of the actually proper values (see wiki article).
 * 
 * @author Alexandre Bouchard (alexandre.bouchard@gmail.com)
 *
 */
public class NIWHyperParameter implements HyperParameter
{
  /**
   * Creates a NIWHyperParameter with default vague values.
   * @param dim The dimensionality of each observation.
   * @return
   */
  public static NIWHyperParameter withDimensionality(int dim)
  {
    double nu = dim;
    double kappa = 0.001;
    SimpleMatrix scriptV = new SimpleMatrix(dim, 1);
    SimpleMatrix delta = new SimpleMatrix(dim, dim);
    for (int d = 0; d < dim; d++)
      delta.set(d,d, 1.0);
    return new NIWHyperParameter(kappa, scriptV, nu, delta);
  }
  
  /**
   * Scale parameter of the random mean.
   * 
   * Higher values can be interpreted as  
   * the distribution on the mean being 
   * stronger (more confident). 
   * 
   * Should be strictly greater than zero.
   */
  private double kappa;
  
  /**
   * Location parameter of the mean.
   * (In fact, mean of the mean).
   */
  private SimpleMatrix scriptV;
  
  /**
   * Scale parameter of the random covariance.
   * 
   * Higher values can be interpreted as  
   * the distribution on the covariance 
   * being stronger  (more confident). 
   * 
   * Should be greater than the dimensionality - 1
   */
  private double nu;
  
  /**
   * Location parameter of the random covariance
   * 
   * Should be positive definite.
   */
  private SimpleMatrix delta;
  
  /**
   * 
   * @param kappa
   * @param scriptV
   * @param nu
   * @param delta
   */
  public NIWHyperParameter(
      double kappa, 
      SimpleMatrix scriptV,
      double nu, 
      SimpleMatrix delta)
  {
    this.kappa = kappa;
    this.scriptV = scriptV;
    this.nu = nu;
    this.delta = delta;
  }

  /**
   * Scale parameter of the random covariance.
   * 
   * Higher values can be interpreted as  
   * the distribution on the covariance 
   * being stronger  (more confident). 
   * 
   * Should be greater than the dimensionality - 1
   */
  public double nu() 
  {
    return nu;
  }
  
  /**
   * Location parameter of the random covariance
   * 
   * Should be positive definite.
   */
  public SimpleMatrix delta()
  {
    return delta;
  }
  
  /**
   * Location parameter of the mean.
   * (In fact, mean of the mean).
   */
  public SimpleMatrix scriptV()
  {
    return scriptV;
  }
  
  /**
   * Scale parameter of the random mean.
   * 
   * Higher values can be interpreted as  
   * the distribution on the mean being 
   * stronger (more confident). 
   * 
   * Should be strictly greater than zero.
   */
  public double kappa()
  {
    return kappa;
  }

  /**
   * @return The dimensionality of each observation.
   */
  public int dim()
  {
    return delta().numRows();
  }

  /**
   * 
   * @param kappa
   */
  public void setKappa(double kappa)
  {
    this.kappa = kappa;
  }

  /**
   * 
   * @param nu
   */
  public void setNu(double nu)
  {
    this.nu = nu;
  }
  
  /**
   * Used by the generic MH machinery to resample hyper-parameter kappa
   * @return
   */
  public RealVariable kappaVariableView()
  {
    return new RealVariable() {
      @Override public void setValue(double newValue) { kappa = newValue; }
      @Override  public double getValue()             { return kappa; }
    };
  }
  
  /**
   * Used by the generic MH machinery to resample hyper-parameter nu
   * @return
   */
  public RealVariable nuVariableView()
  {
    return new RealVariable() {
      @Override public void setValue(double newValue) { nu = newValue; }
      @Override  public double getValue()             { return nu; }
    };
  }

  /**
   * Creates a deep cloned copy.
   * @param hp
   * @return
   */
  public static NIWHyperParameter copyOf(NIWHyperParameter hp)
  {
    return new NIWHyperParameter(hp.kappa, hp.scriptV.copy(), hp.nu, hp.delta.copy());
  }

}
