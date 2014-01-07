package zengkunceju;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ProjectionUtil
{
  private static Ellipse[] ells;
  String name;
  Ellipse useEllipse;
  double a;
  public ProjectionUtil()
  {
    ells = new Ellipse[8];
    ells[0] = new Ellipse("Sphere", 180 * 60 / Math.PI, "Infinite");
    ells[1] = new Ellipse("WGS84", 6378.137 / 1.852, 298.257223563);
    ells[2] = new Ellipse("NAD27", 6378.2064 / 1.852, 294.9786982138);
    ells[3] = new Ellipse("International", 6378.388 / 1.852, 297.0);
    ells[4] = new Ellipse("Krasovsky", 6378.245 / 1.852, 298.3);
    ells[5] = new Ellipse("Bessel", 6377.397155 / 1.852, 299.1528);
    ells[6] = new Ellipse("WGS72", 6378.135 / 1.852, 298.26);
    ells[7] = new Ellipse("WGS66", 6378.145 / 1.852, 298.25);
    useEllipse = ells[0];

  }

  public int ComputeFormCD(double lat1, double lon1, double lat2 , double lon2) {
    double dc = 1.852;
    lon1 = (Math.PI / 180) * lon1;
    lon2 = (Math.PI / 180) * lon2;
    lat1 = (Math.PI / 180) * lat1;
    lat2 = (Math.PI / 180) * lat2;
    /* get distance conversion factor */
    if (useEllipse.name.equals("Sphere")) {
      // spherical code
      double d = crsdist(lat1, lon1, lat2, lon2) * (180 / Math.PI) * 60 * dc;
      return (int) (d * 1000);
    }
    else {
      double d=this.crsdist_ell(lat1, lon1, lat2, lon2);
      return (int)(dc*d*1000);
    }
  }

  public  double crsdist(double lat1, double lon1, double lat2, double lon2) {
    // radian args
    /* compute course and distance (spherical) */

    if ( (lat1 + lat2 == 0.) && (Math.abs(lon1 - lon2) == Math.PI)) {
      return 0;
    }
    double t1=Math.sin(lat1) * Math.sin(lat2) ;
    double t2=Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2);
    double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
                  Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    return d;
  }

 public  double crsdist_ell(double glat1, double glon1, double glat2, double glon2) {

    double a = useEllipse.a;
    double f = 1 / useEllipse.invf;
    double r = 0, tu1 = 0, tu2 = 0, cu1 = 0, su1 = 0, cu2 = 0, s1 = 0, b1 = 0,
        f1 = 0;
    double x = 0, sx = 0, cx = 0, sy = 0, cy = 0, y = 0, sa = 0, c2a = 0,
        cz = 0, e = 0, c = 0, d = 0;
    double EPS = 0.00000000005;
    double faz = 0, baz = 0, s = 0;
    double iter = 1;
    double MAXITER = 100;
    if ( (glat1 + glat2 == 0.) && (Math.abs(glon1 - glon2) == Math.PI)) {
      glat1 = glat1 + 0.00001; // allow algorithm to complete
    }
    r = 1 - f;
    tu1 = r * Math.tan(glat1);
    tu2 = r * Math.tan(glat2);
    cu1 = 1. / Math.sqrt(1. + tu1 * tu1);
    su1 = cu1 * tu1;
    cu2 = 1. / Math.sqrt(1. + tu2 * tu2);
    s1 = cu1 * cu2;
    b1 = s1 * tu2;
    f1 = b1 * tu1;
    x = glon2 - glon1;
    d = x + 1; // force one pass
    while ( (Math.abs(d - x) > EPS) && (iter < MAXITER)) {
      iter = iter + 1;
      sx = Math.sin(x);
//       alert("sx="+sx)
      cx = Math.cos(x);
      tu1 = cu2 * sx;
      tu2 = b1 - su1 * cu2 * cx;
      sy = Math.sqrt(tu1 * tu1 + tu2 * tu2);
      cy = s1 * cx + f1;
      y = atan2(sy, cy);
      sa = s1 * sx / sy;
      c2a = 1 - sa * sa;
      cz = f1 + f1;
      if (c2a > 0.) {
        cz = cy - cz / c2a;
      }
      e = cz * cz * 2. - 1.;
      c = ( ( -3. * c2a + 4.) * f + 4.) * c2a * f / 16.;
      d = x;
      x = ( (e * cy * c + cz) * sy * c + y) * sa;
      x = (1. - c) * x * f + glon2 - glon1;
    }
    faz = modcrs(atan2(tu1, tu2));
    baz = modcrs(atan2(cu1 * sx, b1 * cx - su1 * cu2) + Math.PI);
    x = Math.sqrt( (1 / (r * r) - 1) * c2a + 1);
    x += 1;
    x = (x - 2.) / x;
    c = 1. - x;
    c = (x * x / 4. + 1.) / c;
    d = (0.375 * x * x - 1.) * x;
    x = e * cy;
    s = ( ( ( (sy * sy * 4. - 3.) * (1. - e - e) * cz * d / 6. - x) * d / 4. +
           cz) * sy * d + y) * c * a * r;
    return s;
  }

  double acosf(double x) {
    /* protect against rounding error on input argument */
    if (Math.abs(x) > 1) {
      x /= Math.abs(x);
    }
    return Math.acos(x);
  }

  double modcrs(double x) {
    return mod(x, 2 * Math.PI);
  }

  double mod(double x, double y) {
    return x - y * (int) (x / y);

  }


double atan2(double y, double x) {
    double out = 0;
    if (x < 0) {
  out = Math.atan(y / x) + Math.PI;
}
if ( (x > 0) && (y >= 0)) {
  out = Math.atan(y / x);
}
if ( (x > 0) && (y < 0)) {
  out = Math.atan(y / x) + 2 * Math.PI;
}
if ( (x == 0) && (y > 0)) {
  out = Math.PI / 2;
}
if ( (x == 0) && (y < 0)) {
  out = 3 * Math.PI / 2;
}
if ( (x == 0) && (y == 0)) {
  out = 0.;
}
return out;
}

class Ellipse
  {
  String name;
  double a;
  double invf;
  public Ellipse(String name, double b, double invf) {
    this.name = name;
    this.a = b;
    this.invf = invf;
  }

  public Ellipse(String name, double b, String invf) {
    this.name = name;
    this.a = b;
  }

}

}
