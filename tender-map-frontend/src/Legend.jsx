const Legend = ({ title, text, items }) => {
  const legendStyle = {
    display: 'flex',
    flexDirection: 'column',
    marginTop: '20px',
    width: '20vw', // Set the fixed width here
  };

  const legendItemStyle = {
    display: 'flex',
    alignItems: 'center',
    marginBottom: '8px',
  };

  const legendColorStyle = {
    width: '16px',
    height: '16px',
    borderRadius: '50%',
    marginRight: '8px',
  };

  const legendLabelStyle = {
    fontSize: '14px',
  };

  return (
    <div style={legendStyle}>
      <h3>{title}</h3>
      <p>{text}</p>
      {items.map((item, index) => (
        <div key={index} style={legendItemStyle}>
          <span
            style={{ ...legendColorStyle, backgroundColor: item.color }}
          ></span>
          <span style={legendLabelStyle}>{item.label}</span>
        </div>
      ))}

    </div>
  );
};

export default Legend;
