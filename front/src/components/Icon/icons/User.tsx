import * as React from 'react';

const SvgUser = (props: React.SVGProps<SVGSVGElement>): JSX.Element => (
  <svg
    width={34}
    height={34}
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      d="M16.907 0A16.925 16.925 0 0 0 0 16.907c0 4.518 1.758 8.763 4.95 11.956a16.795 16.795 0 0 0 11.957 4.952 16.925 16.925 0 0 0 16.908-16.907A16.925 16.925 0 0 0 16.908 0ZM6.687 28.753a12.735 12.735 0 0 1 9.895-4.973c.06.002.118.017.178.017h.057c.055 0 .105-.015.16-.017a12.716 12.716 0 0 1 9.98 5.115 15.565 15.565 0 0 1-10.05 3.67 15.53 15.53 0 0 1-10.22-3.812Zm10.08-6.236c-.062 0-.122.01-.184.013a5.607 5.607 0 0 1-5.418-5.638 5.614 5.614 0 0 1 5.655-5.595 5.614 5.614 0 0 1 5.595 5.655c0 3.03-2.425 5.493-5.435 5.58-.07-.002-.14-.015-.213-.015Zm11.118 5.538a13.985 13.985 0 0 0-7.79-5.13 6.873 6.873 0 0 0 3.57-6.03 6.866 6.866 0 0 0-6.905-6.845 6.867 6.867 0 0 0-5.883 10.373 6.825 6.825 0 0 0 2.593 2.497 14.005 14.005 0 0 0-7.703 4.987 15.55 15.55 0 0 1-4.515-10.994A15.675 15.675 0 0 1 16.91 1.255a15.675 15.675 0 0 1 15.658 15.658 15.618 15.618 0 0 1-4.683 11.142Z"
      fill="currentColor"
    />
  </svg>
);

export default SvgUser;
