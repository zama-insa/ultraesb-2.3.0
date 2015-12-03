AdroitLogic UltraESB

Copyright (c) 2010-2014 AdroitLogic Private Ltd. All Rights Reserved

==== Release Notes =====================================================================================================

** Please refer the RELEASE_NOTES.TXT for important updates about each release **


==== Getting Started guides, Samples to get started, and Full Reference Documentation ==================================

Please refer to the documentation from http://docs.adroitlogic.org for screencasts, introductory guides, samples and
full reference documentation. The mediation API documentation can be found at http://api.adroitlogic.org

Annotated sample configurations are provided in the samples/conf directory, and maybe started by passing the
sample number to the ultraesb.sh/.bat script as: "bin/ultraesb.sh -sample <sample_number>"


==== Running the UltraESB Web based Administration Console =============================================================

Execute the uconsole.sh/.bat script to start the Web based Administration Console (UConsole). It will be available on
the localhost over HTTPS on port 8043 by default at the URL : https://localhost:8043/uconsole

By default the administration console binds only on the local network interface - unless you change the
uconsole/conf/jetty.xml file and update the "Host" and/or "Port" attributes. You may execute the UConsole on a remote
system, and connect over JMX to any UltraESB server instance. (e.g. deploy the UltraESB on the enterprise server, and
execute the console on your personal desktop)


==== Running the UltraESB Command Line Interface Client ================================================================

Execute the uterm.sh/.bat script to start the Command Line Interface client (UTerm). The UTerm is a command line client
to interact with the UltraESB instances or a cluster. It can be used in an interactive mode as well as a scriptable
mode.

By default, it can connect to local UltraESB instances (i.e. on the same server) without authentication - similar to the
jconsole. To configure the default JMX connection to be used, edit the conf/management/uterm.properties file as necessary.

By default a command executes against the connected instance. Most commands support the "-w" option to apply the command
across the cluster, when clustering is enabled.


==== Checking out the complete Source Code =============================================================================

The full and complete source code of the UltraESB is released under the OSI approved GNU Affero General Public License 
[AGPL]. Clone the full UltraESB Mercurial repository with the following command:

    hg clone https://bitbucket.org/adroitlogic/ultraesb

Clone the full ToolBox Mercurial repository with the following command:

    hg clone https://bitbucket.org/adroitlogic/toolbox


==== Building the binaries yourself under the Open Source AGPL License =================================================

You may download the source code directly from the Merurial repository (current development tip or a tag), or download 
the source distributions from the AdroitLogic website, to build the binaries yourself.

When you build the binary from an AGPL licensed source code, you fall only under the terms of the OSI approved GNU 
Affero General Public License [AGPL]

To build the binaries, you will require the Java Development Kit v1.6.x and Maven 3 or later. Follow instructions in the
BUILDING.TXT file for more details. The first time you build the UltraESB, Maven will download dependency JAR files 
from public Maven repositories, and may take a few minutes.


==== Binary Releases ===================================================================================================

Binary versions of the UltraESB are released under a zero-dollar, non-GPL commercial license, that allows free, unlimited
and perpetual production use at no cost. The binary releases contains  artifacts only under the following licenses, and 
does NOT include AGPL or GPL licensed artifacts.

    * Apache Software Foundation v1.1 and v2.0
    * MIT License and MIT style licenses
    * BSD License and BSD style license
    * CDDL v1.0
    * MPL v1.1
    * GNU LGPL v2.1 and v3.0
    * AdroitLogic Zero-Dollar License (See LICENSE-FREE.TXT)

Email the following information to license@adroitlogic.com to obtain your free, unlimited and perpetual license.

 - Name of the Licensee
 - Number of Authorized Instances
 - Server ID

Refer to the website for more details


==== Free Community Support ============================================================================================

Free community support for the UltraESB is provided via the Community Forum (https://getsatisfaction.com/adroitlogic/).
To report any issues or feature requests, please file an entry on the Issue tracker
(https://bitbucket.org/adroitlogic/ultraesb/issues).


==== Commercial Support ================================================================================================

AdroitLogic provides optional commercial support for a fee - including production support, consultancy, professional 
services, custom extensions and training. However, if the  source code has been modified by you, our potential to help
you may be limited. Thus we encourage you to consult us before developing any extensions.


==== Performance Comparison ============================================================================================

Benchmark the UltraESB against any other ESB - We've now made it a trivial task for anyone to do this for a under $10
of computing time on the Amazon EC2 - so that you are not dependent on *any* vendor optimized hardware, configuration
or biased tests or results. Check out http://esbperformance.org


==== About AdroitLogic =================================================================================================

AdroitLogic Private Ltd. is a privately held, innovative technology company based in Singapore, that believes in, 
and proves, that better design and clever code yields much better software. AdroitLogic is driven by the personal 
dedication and commitment of its founders, who are personally involved with the design, development and support of 
its products.
